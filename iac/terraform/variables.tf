variable "proxmox_endpoint" {
  type = string
}

variable "proxmox_ssh_address" {
  type        = string
  description = "SSH address of the Proxmox node used by the provider."
}

variable "target_node" {
  type = string
}

variable "ubuntu_image_file_id" {
  type        = string
  description = "Proxmox file id of the Ubuntu disk image. Example: local:iso/ubuntu-server.img."

  validation {
    condition     = length(trimspace(var.ubuntu_image_file_id)) > 0
    error_message = "ubuntu_image_file_id must not be empty."
  }
}

variable "upload_local_image" {
  type        = bool
  default     = false
  description = "If true, upload a local image to the Proxmox datastore using `local_image_path`/`local_image_file_name`."
}

variable "local_image_path" {
  type        = string
  default     = ""
  description = "Local path to the image file to upload when `upload_local_image` is true (e.g. ./images/ubuntu.qcow2)."

  validation {
    condition     = !var.upload_local_image || length(trimspace(var.local_image_path)) > 0
    error_message = "When upload_local_image is true, local_image_path must not be empty."
  }
}

variable "local_image_file_name" {
  type        = string
  default     = ""
  description = "Target file name to use in the Proxmox datastore when uploading the local image (e.g. ubuntu-jammy-cloudimg-amd64.qcow2)."

  validation {
    condition     = !var.upload_local_image || length(trimspace(var.local_image_file_name)) > 0
    error_message = "When upload_local_image is true, local_image_file_name must not be empty."
  }
}

variable "local_image_file_format" {
  type        = string
  default     = "qcow2"
  description = "File format of the uploaded image (qcow2, vmdk, etc.). Used to set disk.file_format when using an uploaded image."
}

#check "ubuntu_image_file_is_cloud_image" {
# assert {
# Fail if the value looks like a Proxmox ISO path (contains ':iso' or ':iso/')
#  condition     = !can(regex(":iso(/|$)", var.ubuntu_image_file_id)) || regex(" :iso(/|$)", " ") == " "
# error_message = "variable 'ubuntu_image_file_id' appears to reference an installer ISO (contains ':iso'). Use a qcow2 cloud-image stored in a storage that supports VM images (example: 'local:images/ubuntu-jammy-cloudimg-amd64.qcow2' or 'local-lvm:...')."
# }
#}

variable "api_token" {
  type      = string
  sensitive = true
}

variable "pmx_root_password" {
  type      = string
  sensitive = true
}

variable "tskey_auth" {
  type      = string
  sensitive = true
}

variable "staging_ssh_pubkey_path" {
  type    = string
  default = "C:\\Users\\noa\\.ssh\\id_ed25519.pub"
}

variable "additional_ssh_pubkeys" {
  type      = list(string)
  sensitive = true
  default   = []
}

variable "vm_name_prefix" {
  type    = string
  default = "portfolio"
}

variable "snippet_datastore" {
  description = "Datastore to use for cloud-init snippets. Usually 'local'."
  type        = string
  default     = "local"
}

variable "vm_datastore" {
  description = "Datastore to use for VM disks and uploaded VM images. Usually 'local-lvm'."
  type        = string
  default     = "local-lvm"
}

variable "vm_specs" {
  type = map(object({
    role        = string
    description = string
    cores       = number
    memory_mb   = number
    disk_gb     = number
  }))

  default = {
    manager = {
      role        = "manager"
      description = "Kubernetes manager"
      cores       = 2
      memory_mb   = 2048
      disk_gb     = 20
    }
    worker1 = {
      role        = "worker1"
      description = "Kubernetes worker1"
      cores       = 1
      memory_mb   = 2048
      disk_gb     = 50
    }
    worker2 = {
      role        = "worker2"
      description = "Kubernetes worker2"
      cores       = 1
      memory_mb   = 2048
      disk_gb     = 50
    }
  }
}
