output "k8s_nodes" {
  description = "Created VMs mapping by key."
  value = {
    for name, vm in proxmox_virtual_environment_vm.projet_metier_vm : name => {
      hostname = vm.name
      role     = var.vm_specs[name].role
      ipv4     = try(vm.ipv4_addresses, [])
    }
  }
}

output "ansible_inventory_hint" {
  description = "Inventory hostnames when Tailscale DNS is enabled."
  value       = [for name in keys(var.vm_specs) : "${var.vm_name_prefix}-${name}"]
}
