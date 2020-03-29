# CloudBees on EKS

## Based On

* https://dzone.com/articles/amazon-aws-eks-and-rds-postgresql-with-terraform-i
* https://github.com/mudrii/

## AWS IAM Setup

* https://github.com/kubernetes-sigs/aws-iam-authenticator

## EKS CTL

```bash
AWS_PROFILE=cloudbees-iam
AWS_ROLE_ARN=
```

```bash
eksctl create cluster  --config-file cluster.yaml --install-vpc-controllers --profile cloudbees-eks
```

### Config File

```yaml
apiVersion: eksctl.io/v1alpha5
kind: ClusterConfig

metadata:
  name: cluster-jvandergriendt
  region: us-east-1
  version: "1.15"
  tags: 
    CreatedBy: jvandergriendt

nodeGroups:
  - name: ng1-masters
    instanceType: m5.large
    minSize: 2
    maxSize: 8
    volumeSize: 100
    volumeType: gp2
    labels:
      nodegroup-type: masters
    iam:
      withAddonPolicies:
        autoScaler: true
  - name: ng2-builds
    minSize: 2
    maxSize: 8
    volumeSize: 100
    volumeType: gp2
    labels:
      nodegroup-type: builds
    iam:
      withAddonPolicies:
        autoScaler: true
    instancesDistribution:
      maxPrice: 0.017
      instanceTypes: ["t3.medium", "t2.medium"] # At least one instance type should be specified
      onDemandBaseCapacity: 0
      onDemandPercentageAboveBaseCapacity: 50
      spotInstancePools: 2
  - name: ng3-windows
    amiFamily: WindowsServer2019FullContainer
    minSize: 1
    maxSize: 2

availabilityZones: ["us-east-1d", "us-east-1f"]
```

### References

* https://eksctl.io/usage/creating-and-managing-clusters/
* https://github.com/weaveworks/eksctl/blob/master/examples/03-two-nodegroups.yaml
* https://github.com/weaveworks/eksctl/blob/master/examples/05-advanced-nodegroups.yaml
* https://github.com/weaveworks/eksctl/blob/master/examples/14-windows-nodes.yaml
* https://eksctl.io/usage/schema/

## Access to EKSCTL Created Resources

```bash
eksctl get iamidentitymapping --region=us-east-1 --cluster=cluster-jvandergriendt
```

First, retrieve the actual VPC used by the eksctl config, via the following command:

```bash
eksctl utils describe-stacks --region=us-east-1 --cluster=cluster-jvandergriendt
```

Look for this snippet:

```json
{
    ExportName: "eksctl-cluster-jvandergriendt-cluster::VPC",
    OutputKey: "VPC",
    OutputValue: "vpc-07669588344f89ae1"
}
```

## Other Terraform Resources

* EFS
* NLB + SSL
* fix ELB by overriding the port from `http 443 -> xyz`, to `tcp 443 -> xyz`

### S3 Bucket

Add below policy to node role of the master nodes.

Go to EC2, select a node of the group, click on the role, and add an inline policy.

```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "backup",
            "Effect": "Allow",
            "Action": [
                "s3:PutObject",
                "s3:DeleteObject"
            ],
            "Resource": [
                "arn:aws:s3:::joostvdg-core-backups/*"
            ]
        },
        {
            "Sid": "restoreListObjects",
            "Effect": "Allow",
            "Action": [
                "s3:ListBucket"
            ],
            "Resource": [
                "arn:aws:s3:::joostvdg-core-backups"
            ]
        },
        {
            "Sid": "restoreGetObject",
            "Effect": "Allow",
            "Action": [
                "s3:GetObject"
            ],
            "Resource": [
                "arn:aws:s3:::joostvdg-core-backups/*"
            ]
        }
    ]
}
```

## Taint Windows Node

```bash
kubectl taint nodes ${WINDOWS_NODE} ostype=windows:NoSchedule
```

It should say:

```bash
node/ip-192-168-26-213.ec2.internal tainted
```

Show taints:

```bash
kubectl get nodes -o json | jq '.items[].spec.taints'
```

* https://github.com/aws/containers-roadmap/issues/463

## Charts



### Bootstrap

#### Nginx

#### EFS

* https://docs.cloudbees.com/docs/cloudbees-core/latest/cloud-reference-architecture/kubernetes-efs
* https://www.terraform.io/docs/providers/aws/d/subnet_ids.html
* https://blog.gruntwork.io/terraform-tips-tricks-loops-if-statements-and-gotchas-f739bbae55f9
* https://ops.tips/gists/how-aws-efs-multiple-availability-zones-terraform/
* https://www.terraform.io/docs/providers/aws/r/efs_mount_target.html

## CloudBees Core

### Master Provisioning

#### YAML

```yaml
apiVersion: "apps/v1"
kind: "StatefulSet"
spec:
  template:
    metadata:
      annotations:
        prometheus.io/path: /${name}/prometheus
        prometheus.io/port: "8080"
        prometheus.io/scrape: "true"
      labels:
        app.kubernetes.io/component: Managed-Master
        app.kubernetes.io/instance: ${name}
        app.kubernetes.io/managed-by: CloudBees-Core-Cloud-Operations-Center
        app.kubernetes.io/name: ${name}
    spec:
      nodeSelector:
        nodegroup-type: masters
```

## References

* https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/general-purpose-instances.html