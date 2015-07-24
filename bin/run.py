#! /usr/bin/env python3

import json
import subprocess
import time
import os

input = "names.txt"

output = "output.json"


def whois(query):
	cmd = "whois -h whois.norid.no "
	cmd += str(query)
	while True:
		output = subprocess.Popen(cmd, shell=True, stdout=subprocess.PIPE)
		output = output.stdout.read()
		if b"No match" in output:
			return None
		elif b"Access denied" not in output:
			return str(output, "latin-1")
		else:
			print("Throttling requests for 20 sec")
			time.sleep(20)

def get_handles(orgnr):
	output = whois(orgnr)	
	output = str(output).split("\n")
	return [i.split(": ")[1] for i in output if "Handle" in i]


def get_domains(handles):
	ret = []
	for i in handles:
		output = whois(i)	
		output = output.split("\n")
		for n in output:
			if "Domains" in n:
				n = n.split(": ")[1].split(" ")
				ret.extend(n)
	return ret



def get_orgnr(domain):
	output = whois(domain)
	if not output:
		return None
	output = output.split("\n")
	return [i.split(": ")[1] for i in output if "Id Number" in i]


def main(domains):
	d = {}
	for i,orgnr in domains.items():
		print("------------")
		d[i] = {"orgnr": orgnr,
				"domains": []}		
		d[i]["orgnr"] = orgnr
		print("Domain: "+i)
		print("Orgnr: "+orgnr)
		handles = get_handles(orgnr)
		print("Handles: "+str(handles))
		d[i]["domains"] = get_domains(handles)
		print("Domains: "+str(d[i]["domains"]))
		if d[i]["domains"] == []:
			del d[i]
	return 


if __name__ == '__main__':
	f = open("output-org-names.json").read()
	f = json.loads(f)
	out = main(f)
	output = open("output.json", "w")
	output.write(json.dumps(out, ensure_ascii=False, indent=4, sort_keys=True))
