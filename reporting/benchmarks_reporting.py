import json
import argparse
import re

import matplotlib.pyplot as plt
import numpy as np

from dataclasses import dataclass
from itertools import groupby
from collections import defaultdict

# Model

@dataclass
class Measurement:
    cls: str
    method: str
    params: list
    score: float
    error: float
    unit: str

# Methods

def plot_metrics(metrics_list, title, figure, width=0.1):

    plt.figure(figure)

    by_input = defaultdict(list)
    by_map = defaultdict(list)

    for e in metrics_list:
        by_input[e.params["input"]].append(e)

    for e in metrics_list:
        by_map[e.params["map"]].append(e)     

    groups = [e for e in by_input.keys()]

    i = 0
    ind = np.arange(len(by_input.keys()))

    for e in by_map.keys(): 
        vals = [elem.score for elem in by_map[e]]
        errs = [elem.error for elem in by_map[e]]
        plt.bar(ind + i*width, vals, width, label=e, yerr=errs)
        i+=1
    
    plt.ylabel("us/op")
    plt.xlabel("Number of elements")
    plt.title(title)
    plt.xticks(np.arange(len(groups)) + 2*width + width/2, groups)
    plt.legend(loc="best")

    plt.savefig(title+".png")

# Initializing arguments

parser = argparse.ArgumentParser(description="Generate benchmarks visual reports")
parser.add_argument("--json", type=str, help="the path to the benchmark json", required=True)

args = parser.parse_args()

print("Parsing benchmark file: {}".format(args.json))

f = open(args.json)
d = json.load(f)

pm = []
for el in d:
    cnm_sliced = el["benchmark"].split(".")[-2:]
    cls = cnm_sliced[0]
    method = cnm_sliced[1]
    params = {}
    params["map"] = el["params"]["mapClass"]
    params["input"] = "".join(re.split("(1)",el["params"]["input"])[-2:])
    score = el["primaryMetric"]["score"]
    error = el["primaryMetric"]["scoreError"]
    unit = el["primaryMetric"]["scoreUnit"]
    pm.append(Measurement(cls=cls, method=method, params=params, score=score, error=error, unit=unit))
    print("[*] {}.{} with score={}".format(cls, method, score))

an_codes_rr = [ m for m in pm if m.cls == "AlphaNumericCodesReads" and m.method == "randomReads" ]    
an_codes_rrwm = [ m for m in pm if m.cls == "AlphaNumericCodesReads" and m.method == "randomReadsWithMisses" ]
rs_rr = [ m for m in pm if m.cls == "RandomStringsReads" and m.method == "randomReads" ]    
rr_rrwm = [ m for m in pm if m.cls == "RandomStringsReads" and m.method == "randomReadsWithMisses" ]
ss_rr = [ m for m in pm if m.cls == "SequencedStringReads" and m.method == "randomReads" ]    
ss_rrwm = [ m for m in pm if m.cls == "SequencedStringReads" and m.method == "randomReadsWithMisses" ]

plot_metrics(an_codes_rr, "AlphaNumericCodesReads.randomReads", figure=100)
plot_metrics(an_codes_rrwm, "AlphaNumericCodesReads.randomReadsWithMisses", figure=200)
plot_metrics(rs_rr, "RandomStringsReads.randomReads", figure=300)
plot_metrics(rr_rrwm, "RandomStringsReads.randomReadsWithMisses", figure=400)
plot_metrics(ss_rr, "SequencedStringReads.randomReads", figure=500)
plot_metrics(ss_rrwm, "SequencedStringReads.randomReadsWithMisses", figure=600)

# plot_metric2()