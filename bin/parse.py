import json
import csv


# 1510, 1520,6500, kommuner

codes = [1110, 1120, 3900, 6100]

out = {}


with open("enhetsregisteret.csv", newline="") as f:
    enhetsreader = csv.DictReader(f, delimiter=";", quotechar="\"")
    for row in enhetsreader:
    	if row["sektorkode"] != "" and int(row["sektorkode"]) in codes:
    		out[row["navn"]] = row["\ufefforgnr"]

output = open("output-org-names.json", "w")
output.write(json.dumps(out, ensure_ascii=False, indent=4, sort_keys=True))