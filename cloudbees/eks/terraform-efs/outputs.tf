output "mount-target-dns" {
    description = "Address of the mount target provisioned."
    value = [for s in aws_efs_mount_target.eks : s.dns_name]
}