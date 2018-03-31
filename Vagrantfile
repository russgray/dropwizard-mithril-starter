# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant.configure("2") do |config|
    config.vm.box = "ubuntu/xenial64"
    config.vm.network "private_network", ip: "192.168.63.101"
    
    config.vm.provider "virtualbox" do |vb|
      vb.memory = "1024"
      vb.cpus = 1
    end
  
    if Vagrant.has_plugin?("vagrant-multi-putty")
      config.putty.session = "vagrant-default"
    end
  
    config.vm.provision "ansible_local" do |ansible|
      ansible.verbose = "v"
      ansible.install = true
      ansible.galaxy_role_file = "requirements.yml"
      ansible.playbook = "playbook.yml"
    end
  end
  