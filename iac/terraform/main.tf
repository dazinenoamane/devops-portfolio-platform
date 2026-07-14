locals {
  ssh_key_content_raw = trimspace(data.local_file.ssh_key.content)
  ssh_key_content     = sensitive(local.ssh_key_content_raw)
  ssh_key_valid       = can(regex("^ssh-", local.ssh_key_content_raw))
}

data "local_file" "ssh_key" {
  filename = var.staging_ssh_pubkey_path
}

check "ssh_key_format" {
  assert {
    condition     = local.ssh_key_valid
    error_message = "Invalid SSH public key format"
  }
}


resource "proxmox_virtual_environment_file" "cloud_config" {
  for_each     = var.vm_specs
  content_type = "snippets"
  datastore_id = var.snippet_datastore
  node_name    = var.target_node

  source_raw {
    data = templatefile("${path.module}/cloud_config.yml.tpl", {
      tskey_auth         = var.tskey_auth,
      hostname           = "${var.vm_name_prefix}-${each.key}",
      staging_ssh_pubkey = local.ssh_key_content,
      other_ssh_keys     = var.additional_ssh_pubkeys,
      node_role          = each.value.role
    })
    file_name = "${var.vm_name_prefix}-${each.key}-cloud-config.yaml"
  }
}

resource "proxmox_virtual_environment_vm" "projet_metier_vm" {
  for_each    = var.vm_specs
  name        = "${var.vm_name_prefix}-${each.key}"
  description = each.value.description
  node_name   = var.target_node
  initialization {
    dns {
      servers = ["1.1.1.1"]
    }

    ip_config {
      ipv4 {
        address = "dhcp"
      }
    }

    user_data_file_id = proxmox_virtual_environment_file.cloud_config[each.key].id
  }
  cpu {
    cores = each.value.cores
    type  = "host"
  }

  memory {
    dedicated = each.value.memory_mb
  }

  network_device {
    bridge = "vmbr0"
  }

  disk {
    datastore_id = var.vm_datastore
    interface    = "scsi0"
    size         = each.value.disk_gb
    file_id      = var.ubuntu_image_file_id
  }

  serial_device {
    device = "socket"
  }

  boot_order = ["scsi0"]
}

// Optional upload of a local qcow2 image to the Proxmox datastore. Controlled by `var.upload_local_image`.
resource "proxmox_virtual_environment_file" "cloud_image" {
  count        = var.upload_local_image ? 1 : 0
  content_type = "iso"
  datastore_id = var.vm_datastore
  node_name    = var.target_node

  source_file {
    file_name = var.local_image_file_name
    path      = var.local_image_path
  }
}
