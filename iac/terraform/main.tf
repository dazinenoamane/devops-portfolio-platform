resource "proxmox_virtual_environment_vm" "ubuntu_vm" {
  name      = "terraform-provider-proxmox-ubuntu-vm"
  node_name = "fanida"

  cpu {
    cores = 2
  }

  memory {
    dedicated = 2048
  }

  # 1. Montage de l'ISO sur un lecteur virtuel CD-ROM
  cdrom {
    file_id   = "local:iso/ubuntu_server.iso"
    interface = "ide3"
  }

  # 2. Création d'un disque vierge qui va accueillir l'installation
  disk {
    datastore_id = "local-lvm"
    interface    = "scsi0"
    size         = 20 # Taille du disque en Go (obligatoire pour un disque vierge)
  }
}