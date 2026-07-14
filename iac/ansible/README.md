# Ansible

This folder prepares the Proxmox VMs after Terraform.

What it does:
- installs common packages
- installs Docker Engine and the Compose plugin
- installs Kubernetes tools
- clones the application repository on the manager node

Run it from this directory:
```bash
ansible-playbook -i inventory/hosts.ini site.yml
```

Before running:
- replace `repo_url` in `group_vars/all.yml`
- update the inventory IP addresses
- make sure SSH access works with the `ubuntu` user and your private key
