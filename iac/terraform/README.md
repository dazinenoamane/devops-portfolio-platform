# Terraform / Proxmox notes

This folder contains Terraform configuration that provisions VMs on Proxmox and uploads cloud-init snippets.

Important notes to get cloud-init provisioning working:

- Use a Proxmox cloud image (qcow2), not an installer ISO. Set `ubuntu_image_file_id` in `terraform.tfvars` to a cloud-image file already present in Proxmox, e.g.: `local:images/ubuntu-jammy-22.04-server-cloudimg-amd64.qcow2`.
- Alternatively you can enable automatic upload of a local image by setting:

```hcl
upload_local_image = true
local_image_path = "./images/ubuntu-jammy-cloudimg-amd64.qcow2"
local_image_file_name = "ubuntu-jammy-cloudimg-amd64.qcow2"
local_image_file_format = "qcow2"
```

- The Proxmox provider uploads snippets (cloud-init) using SFTP which requires a PAM account and an SSH agent available on the machine running Terraform. On Windows (PowerShell) start the `ssh-agent` and add your private key before `terraform apply`:

```powershell
# Start the ssh-agent service (run as Administrator if necessary)
Start-Service ssh-agent
# Add your private key
ssh-add $env:USERPROFILE\.ssh\id_ed25519
# Verify the agent has the key
ssh-add -L
```

- If you previously used an installer ISO (example `local:iso/ubuntu-server.img`) the VM boots the installer and cloud-init will not run. Replace that value with a cloud-image as shown above.

- This module forces the VM boot order to the primary disk (`scsi0`) and configures a serial socket to avoid kernel panic when resizing cloud-image disks on Ubuntu/Debian.

Run:

```powershell
cd iac/terraform
terraform init
terraform plan -var-file="terraform.tfvars"
terraform apply -var-file="terraform.tfvars" -auto-approve
```

If you want, I can help upload a qcow2 image from a local path and re-run `terraform apply` (requires the machine running Terraform to have the image file available and `upload_local_image = true`).
