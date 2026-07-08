variable "proxmox_endpoint" {
    type = string
}
variable "api_token" {
    type = string
    sensitive = true
}
variable "ssh_public_key" {
    type = string
}
variable "vm_root_password" {
    type = string
    sensitive = true
}
