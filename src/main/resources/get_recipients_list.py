#!/usr/bin/env python

from Crypto.Cipher import DES3
from Crypto import Random 
import os, binascii, ast
import pycurl, sys
from StringIO import StringIO

def decrypt(encrypted_string, key):
	encryptor = DES3.new(key, DES3.MODE_CFB, '00000000')
	raw_string = encryptor.decrypt(binascii.a2b_base64(encrypted_string))
	return ast.literal_eval(raw_string)


def get_decryption_key_and_url(application_properties_path):
	result = {}
	with open(application_properties_path, 'r') as search:
		for line in search:
			line = line.rstrip()  # remove '\n' at end of line
			if "decryption.key" in line:
				result['key'] = line.split('decryption.key=')[1]
			if "sms_registration_endpoint" in line:
				result['endpoint'] = line.split('sms_registration_endpoint=')[1]
	return result


def get_encrypted_json(endpoint):
	buffer = StringIO()
	c = pycurl.Curl()
	c.setopt(c.URL, endpoint)
	c.setopt(c.WRITEDATA, buffer)
	c.perform()
	c.close()
	body = buffer.getvalue()
	return body

# Retrieving the decryption key and endpoint URL from application.properties
application_properties_path=sys.argv[1]
info = get_decryption_key_and_url(application_properties_path)

# get encrypted JSON response, from MyParliament URL endpoint
enc_response = get_encrypted_json(info['endpoint'])
enc_array = ast.literal_eval(enc_response)

# decryption of the json file
response = []
for user in enc_array:
	response.append(decrypt(user, info['key']))
	
# 'print' the decrypted response to send it back to the Spring app.
print response
