#!/usr/bin/env python

import paramiko
import sys

magti_command_to_send=sys.argv[1]
smsserver_ip=sys.argv[2]
smsserver_username=sys.argv[3]
smsserver_password=sys.argv[4]

client = paramiko.SSHClient()
client.set_missing_host_key_policy(paramiko.AutoAddPolicy())
client.connect(smsserver_ip, username=smsserver_username, password=smsserver_password)
stdin, stdout, stderr = client.exec_command(magti_command_to_send)
result = ''

for line in stdout:
    result = line.strip('\n')
client.close()

print result
