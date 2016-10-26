#! /usr/bin/env python
import sys
import os
import re

sched_limit = 500000


def read_logs(directory):
    """
    :type directory: str
    """
    files = [os.path.join(directory, f) for f in os.listdir(directory) if os.path.isfile(os.path.join(directory, f)) and f.find(".o") != -1]
    exec_num_matcher = re.compile('----FINISHED EXECUTION (\d*)')
    lazy_hash_matcher = re.compile('Lazy   Hash: (-?\d*)')

    hashes_dpor = set()
    ':type: set of int'
    hashes_ldpor = set()
    ':type: set of int'
    bench_name = f.split('.')[0]
    lazy_hash = 0
    got_hash = False
    doing_lazy_dpor = False
    exec_count = 0
    real_executions = False

    for f in files:
        with open(f, 'r') as fh:
            for line in fh:
                if line.startswith('Lazy DPOR is:'):
                    if line.strip().endswith('false'):
                        doing_lazy_dpor = False
                        hashes_dpor.clear()
                    elif line.strip().endswith('true'):
                        doing_lazy_dpor = True
                        hashes_ldpor.clear()
                    else:
                        assert False
                elif line.startswith('End of real executions.'):
                    real_executions = False
                elif line.startswith('Benchmark name is:'):
                    bench_name = line.split(':')[1].strip()
                    real_executions = True
                    exec_count = 0
                elif real_executions:
                    if line.startswith('Starting new execution!'):
                        got_hash = False
                    elif line.startswith('----FINISHED EXECUTION'):
                        m = exec_num_matcher.match(line)
                        exec_num = int(m.group(1))
                        exec_count += 1
                        assert exec_num == exec_count
                        if got_hash:
                            if doing_lazy_dpor:
                                hashes_ldpor.add(lazy_hash)
                            else:
                                hashes_dpor.add(lazy_hash)
                    elif line.startswith('Lazy   Hash:'):
                        m = lazy_hash_matcher.match(line)
                        h = int(m.group(1))
                        assert not got_hash
                        got_hash = True
                        lazy_hash = h
        for h in hashes_dpor:
            if not h in hashes_ldpor:
                print(bench_name + ": ldpor is missing a hash found by dpor!\n")
                if exec_count == sched_limit:
                    print("   ...but ldpor did not complete\n")

if __name__ == "__main__":
    def main():
        results_dir = sys.argv[1]
        read_logs(results_dir)
    main()