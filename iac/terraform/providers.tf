terraform {
  required_providers {
    proxmox = {
      source  = "bpg/proxmox"
      version = "~> 0.111"
    }
    local = {
      source  = "hashicorp/local"
      version = "~> 2.0"
    }
  }
}

provider "proxmox" {
  endpoint  = var.proxmox_endpoint
  api_token = var.api_token
  insecure  = true

  ssh {
    username = "root"
    password = var.pmx_root_password
    agent    = true

    node {
      name    = var.target_node
      address = var.proxmox_ssh_address
    }
  }
}
