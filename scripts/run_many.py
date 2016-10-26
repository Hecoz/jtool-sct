#! /usr/bin/env python

import os
import sys
import subprocess
from os import path
import errno
from shutil import copy2
import shutil
import threading
import re
import time
import signal

def mkdir_p(path):
    try:
        os.makedirs(path)
    except OSError as exc: # Python >2.5
        if exc.errno == errno.EEXIST and os.path.isdir(path):
            pass
        else: raise

# tests list mode outdir raceTO raceTimes dbTL pbTL 

def commandLine ():
    from argparse import ArgumentParser, ArgumentDefaultsHelpFormatter
    
    cmdline = ArgumentParser(formatter_class=ArgumentDefaultsHelpFormatter)
    
    # The command-line parser and its options
#     cmdline.add_argument("-",
#                           "--dir",
#                           action="store",
#                           help="run all tests in the directory",
#                           metavar="<DIR>")
    
#     cmdline.add_argument("-f",
#                           "--file",
#                           action="store",
#                           help="run this specific tests in the given directory",
#                           metavar="<FILE>")
    
    cmdline.add_argument("-wt",
                         "--walltime",
                          action="store",
                          type=str,
                          help="max wall time for each test",
                          default="09:00:00")
    
    cmdline.add_argument("-mem",
                         "--mem",
                          action="store",
                          type=str,
                          help="max RAM needed",
                          default="2000mb")
    
    cmdline.add_argument("-ncpus",
                         "--ncpus",
                          action="store",
                          type=str,
                          help="#CPUs needed",
                          default="1")
    
    
    cmdline.add_argument("-start",
                         "--start",
                          action="store",
                          type=int,
                          help="",
                          default=0)
    cmdline.add_argument("-end",
                         "--end",
                          action="store",
                          type=int,
                          help="",
                          default=0)
    
    cmdline.add_argument("-limit",
                         "--limit",
                          action="store",
                          type=int,
                          help="",
                          default=10)
    
    
    
    return cmdline.parse_args()

def call_check(cmd):
    print cmd
    subprocess.check_call(cmd)

if __name__ == "__main__":
    args = commandLine()
    script = "run_single.sh"
   # script = "run_single_race.sh" if mode == "race" else "run_single.sh"
    script = path.join(os.environ["ROOT"], "scripts", script)
    
    #envfile = path.join(os.environ["ROOT"], "env.sh")
    
    outdir = path.join(os.environ["ROOT"],"__results")
    mkdir_p(outdir)

    outfile = path.join(outdir, time.strftime("%Y-%m-%d-%H-%M-%S")+".txt")
    
    for i in range(args.start, args.end+1):
      cmd = ["qsub",
             "-l", "walltime="+args.walltime,
             "-l", "mem="+args.mem,
             "-l", "ncpus="+args.ncpus,
             #"-j", "oe",
             "-N", "bench"+str(i),
              "--",
              script, str(i), str(args.limit), outfile]
      call_check(cmd)


