provider "aws" {
    access_key = var.access_key
    secret_key = var.secret_key
    token = var.aws_session_token
    region = var.region
    version = "~> 2.54.0"
}

# Gathers information about the VPC that was provided
# such that we can know what CIDR block to allow requests
# from and to the FS.
data "aws_vpc" "eks" {
    id = var.vpc_id
}

data "aws_subnet_ids" "eks" {
    vpc_id = var.vpc_id
    filter {
        name   = "tag:kubernetes.io/role/internal-elb"
        values = ["1"] # insert values here
    }
}

data "aws_subnet" "eks" {
    for_each = data.aws_subnet_ids.eks.ids
    id       = each.value
}

# data "aws_subnet" "example" {
#     for_each = data.aws_subnet_ids.eks.ids
#     id       = each.value
# }

# Creates a new empty file system in EFS.
#
# Although we're not specifying a VPC_ID here, we can't have
# a EFS assigned to subnets in multiple VPCs.
#
# If we wanted to mount in a differente VPC we'd need to first
# remove all the mount points in subnets of one VPC and only 
# then create the new mountpoints in the other VPC.
resource "aws_efs_file_system" "eks" {
    creation_token = "efs-for-eks-jvandergriendt"
    provisioned_throughput_in_mibps  = var.mips
    throughput_mode = "provisioned"

    tags = {
        Name = "efs-for-eks-jvandergriendt"
    }
}

# Creates a mount target of EFS in a specified subnet
# such that our instances can connect to it.
#
# Here we iterate over `subnets-count` which indicates
# the length of the `var.subnets` list.
#
# This way we're able to create a mount target for each
# of the subnets, making it available to instances in all
# of the desired subnets.
# https://www.terraform.io/docs/providers/aws/d/subnet_ids.html
resource "aws_efs_mount_target" "eks" {
    for_each = data.aws_subnet_ids.eks.ids

    file_system_id = aws_efs_file_system.eks.id
    subnet_id        = each.value

    security_groups = [
        aws_security_group.efs.id
    ]
}

# Allow both ingress and egress for port 2049 (NFS)
# such that our instances are able to get to the mount
# target in the AZ.
#
# Additionaly, we set the `cidr_blocks` that are allowed
# such that we restrict the traffic to machines that are
# within the VPC (and not outside).
resource "aws_security_group" "efs" {
    name        = "efs-mnt"
    description = "Allows NFS traffic from instances within the VPC."
    vpc_id      = var.vpc_id

    ingress {
        from_port = 2049
        to_port   = 2049
        protocol  = "tcp"

        cidr_blocks = [
        "${data.aws_vpc.eks.cidr_block}",
        ]
    }

    egress {
        from_port = 2049
        to_port   = 2049
        protocol  = "tcp"

        cidr_blocks = [
            "${data.aws_vpc.eks.cidr_block}",
        ]
    }

    tags =  {
        Name = "allow_nfs-ec2"
    }
}