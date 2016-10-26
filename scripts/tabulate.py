#! /usr/bin/env python
from itertools import repeat
import sys
import os
import matplotlib
import matplotlib.figure
import matplotlib.axes
from matplotlib.figure import Figure
from matplotlib.legend import Legend
from matplotlib.markers import MarkerStyle
from matplotlib.patches import Rectangle
from matplotlib.text import Text
from matplotlib.transforms import Affine2D
import matplotlib.pyplot as plt
import re

import matplotlib.text as mtext

matplotlib.rcParams['ps.useafm'] = True
matplotlib.rcParams['pdf.use14corefonts'] = True
matplotlib.rcParams['text.usetex'] = True
matplotlib.rcParams['font.size'] = 14.0


hbr_caching_ignored = set(['sparsematmult-2t'])

class CommonResult(object):
    def __init__(self, num_scheds, num_hbgs, num_lhbgs):
        self.num_scheds = num_scheds
        self.num_hbgs = num_hbgs
        self.num_lhbgs = num_lhbgs
        self.cumulative_num_hbgs = []
        self.cumulative_num_lhbgs = []
        self.percRedundantHBRs = -1
        self.num_lock = -1
        self.saw_may_mutex_deadlock = -1
        self.time = 0
        self.scheds_per_second = -1
        self.violation_detected = False
        self.first_may_mutex_deadlock = -1
        self.first_mutex_deadlock = -1
        self.first_error = -1


class MyResult(object):
    def __init__(self, name, num_sched_points):
        self.name = name
        ':type: str'
        self.num_sched_points = num_sched_points
        self.dpor_res = None
        ':type: CommonResult'
        self.ldpor_res = None
        ':type: CommonResult'
        self.hbg_res = None
        ':type: CommonResult'
        self.lhbg_res = None
        ':type: CommonResult'

    def saw_mutex_deadlock(self):
        return self.dpor_res.first_mutex_deadlock != -1 or self.ldpor_res.first_mutex_deadlock != -1
    def dpor_verified(self):
        return self.dpor_res.num_scheds < sched_limit
    def ldpor_verified(self):
        return not self.ldpor_res.violation_detected and self.ldpor_res.num_scheds < sched_limit

def read_logs(directory, benchmarks, benchmarks_sorted):
    """
    :type directory: str
    :type benchmarks: dict[str, MyResult]
    :type benchmarks_sorted: list of str
    """
    files = [f for f in os.listdir(directory) if os.path.isfile(os.path.join(directory, f))]
    exec_num_matcher = re.compile('----FINISHED EXECUTION (-?\d*)')
    hash_matcher = re.compile('Hash: (-?\d*)')
    for f in files:
        run = 0
        exec_count = 0
        hashes = set()
        ':type: set of int'
        cumulative_num_hashes = [[0], [0]]
        bench_name = f.split('.')[0]
        my_res = benchmarks[bench_name]
        with open(os.path.join(directory, f)) as fh:
            for line in fh:
                if line.startswith('Finished warm up runs.===='):
                    run += 1
                    hashes = set()
                    ':type: set of int'
                    exec_count = 0
                elif line.startswith('----FINISHED EXECUTION'):
                    exec_count += 1
                    m = exec_num_matcher.match(line)
                    exec_num = int(m.group(1))
                    assert exec_count == exec_num
                    cumulative_num_hashes[run-1].append(len(hashes))
                    assert len(cumulative_num_hashes[run-1]) == exec_num + 1
                elif line.startswith('Hash:'):
                    m = hash_matcher.match(line)
                    h = int(m.group(1))
                    hashes.add(h)
        my_res.dpor_res.cumulative_num_hbgs = cumulative_num_hashes[0]
        my_res.ldpor_res.cumulative_num_hbgs = cumulative_num_hashes[1]



sched_limit = 100000

def output_table(benchmarks_sorted, benchmarks):
    """
    :type benchmarks: dict[str, MyResult]
    :type benchmarks_sorted: list of str
    """

    with open("results.tex", 'w') as fh:

        def write_field(s):
            s = str(s)
            fh.write(s)
            fh.write(' & ')

        def write_endline():
            fh.write(' \\' + os.linesep)

        for b in benchmarks_sorted:
            bench = benchmarks[b]
            write_field(bench.name)
            write_field(bench.num_sched_points)
            for common_res in [bench.dpor_res, bench.ldpor_res]:
                write_field(common_res.num_scheds)
                write_field(common_res.num_hbgs)
                write_field(common_res.num_lhbgs)
                write_endline()


sched_limit = 100000


def log_graph_preamble():
    fig, ax = plt.subplots(figsize=(8,4))

    ax.set_yscale('log')
    ax.set_xscale('log')
    ax.set_xlim((1, sched_limit))
    ax.set_ylim((1, sched_limit))
    ax.grid(True, clip_on=False, lw=0.3, alpha=0.4)
    #ax.spines['left'].set_bounds(1, sched_limit)
    #ax.spines['bottom'].set_bounds(1, sched_limit)
    ax.set_xticks([1, 100, 1000, 10000, 100000])
    ax.set_yticks([1, 100, 1000, 10000, 100000])
    ax.set_xticklabels(['1', '100', '1000', '10000', '100000'])
    ax.set_yticklabels(['1', '100', '1000', '10000', '100000'])
    plt.subplots_adjust(right=0.8)

    # diagonal
    ax.plot([1, sched_limit], [1, sched_limit], ":", lw=1, c='k')

    # Hide all spines
    ax.spines['right'].set_visible(False)
    ax.spines['top'].set_visible(False)
    ax.spines['left'].set_visible(False)
    ax.spines['bottom'].set_visible(False)
    # Only show ticks on the left and bottom spines
    ax.yaxis.set_ticks_position('left')
    ax.xaxis.set_ticks_position('bottom')

    ax.set_xlabel("AAAAAAAAAAAAAAAAAAAAAA")
    #, rotation="horizontal"
    ax.set_ylabel("AAAAAAAAAAAAAAAAAAAAAA")
    fig.tight_layout()
    return fig, ax


def log_graph_preamble_small():
    fig, ax = plt.subplots()

    ax.set_yscale('log')
    ax.set_xscale('log')
    ax.set_xlim((1, 10000))
    ax.set_ylim((1, 10000))
    ax.grid(True, clip_on=False, lw=0.3, alpha=0.4)
    #ax.spines['left'].set_bounds(1, sched_limit)
    #ax.spines['bottom'].set_bounds(1, sched_limit)
    ax.set_xticks([1, 100, 1000, 10000])
    ax.set_yticks([1, 100, 1000, 10000])
    ax.set_xticklabels(['1', '100', '1000', '10000'])
    ax.set_yticklabels(['1', '100', '1000', '10000'])
    plt.subplots_adjust(right=0.8)

    # diagonal
    ax.plot([1, sched_limit], [1, 10000], ":", lw=1, c='k')

    # Hide all spines
    ax.spines['right'].set_visible(False)
    ax.spines['top'].set_visible(False)
    ax.spines['left'].set_visible(False)
    ax.spines['bottom'].set_visible(False)
    # Only show ticks on the left and bottom spines
    ax.yaxis.set_ticks_position('left')
    ax.xaxis.set_ticks_position('bottom')

    ax.set_xlabel("AAAAAAAAAAAAAAAAAAAAAA")
    #, rotation="horizontal"
    ax.set_ylabel("AAAAAAAAAAAAAAAAAAAAAA")
    fig.tight_layout()
    return fig, ax

def c_graph_preamble():
    fig, ax = plt.subplots()

    #ax.set_yscale('log')
    ax.set_xscale('log')
    ax.set_xlim((1, sched_limit))
    ax.set_ylim((1, 70))
    ax.grid(True, clip_on=False, lw=0.3, alpha=0.4)
    ax.spines['left'].set_bounds(1, sched_limit)
    ax.spines['bottom'].set_bounds(0, 70)
    ax.set_xticks([1, 100, 1000, 10000, 100000])
    #ax.set_yticks([1, 100, 1000, 10000, 100000])
    ax.set_xticklabels(['1', '100', '1000', '10000', '100000'])
    #ax.set_yticklabels(['1', '100', '1000', '10000', '100000'])
    plt.subplots_adjust(right=0.8)

    # diagonal
    #ax.plot([1, sched_limit], [1, sched_limit], ":", lw=1, c='k')

    # Hide all spines
    ax.spines['right'].set_visible(False)
    ax.spines['top'].set_visible(False)
    ax.spines['left'].set_visible(False)
    ax.spines['bottom'].set_visible(False)
    # Only show ticks on the left and bottom spines
    ax.yaxis.set_ticks_position('left')
    ax.xaxis.set_ticks_position('bottom')

    ax.set_xlabel("AAAAAAAAAAAAAAAAAAAAAA")
    #, rotation="horizontal"
    ax.set_ylabel("AAAAAAAAAAAAAAAAAAAAAA")
    fig.tight_layout()
    return fig, ax


def graph_dpor(filename, benchmarks_sorted, benchmarks):
    """
    :type benchmarks_sorted: list of str
    :type benchmarks: dict[str, MyResult]

    :param filename:
    :param benchmarks_sorted:
    """
    fig, ax = log_graph_preamble()

    ax.set_xlabel("\#HBRs")
    ax.set_ylabel("\#lazy HBRs")

    for (idx, name) in enumerate(benchmarks_sorted):
        bench = benchmarks[name]
        x = [bench.dpor_res.num_hbgs]
        y = [bench.dpor_res.num_lhbgs]

        if x[0] == 0:
            x[0] = 1
        if y[0] == 0:
            y[0] = 1

        assert x[0] != 0
        assert y[0] != 0

        m = 'o' if bench.dpor_res.num_scheds < sched_limit else 'x'

        # a = ax.scatter(x[0], y[0], s=50, lw=1, edgecolor="k", facecolor='none', marker=m, alpha=1, clip_on=False)

        strfrm = r'$\textbf{{{0}}}$'
        if bench.dpor_res.num_scheds >= sched_limit:
            strfrm = r'$\underline{{\textbf{{{0}}}}}$'
            # a = ax.scatter(x[0], y[0], s=50, lw=0.5, edgecolor="k", facecolor='none', marker='o', alpha=0.5, clip_on=False)

        temp = ax.text(x[0], y[0], strfrm.format(idx+1), size="7", ha="center", fontweight='700', alpha=1)
        temp.set_transform(temp.get_transform() + Affine2D().translate(0, -2.3)) #-2.3 for size 7, -1.8 for size 5

        #temp = ax.text(x[0], y[0], " "+str(name), size="2", ha="center", alpha=0.8)
        #temp.set_transform(temp.get_transform() + Affine2D().translate(0, 4))

    fig.savefig(filename, bbox_inches=0)


def graph_ldpor_vs_dpor(filename, benchmarks_sorted, benchmarks):
    """
    :type benchmarks_sorted: list of str
    :type benchmarks: dict[str, MyResult]

    :param filename:
    :param benchmarks_sorted:
    """
    fig, ax = log_graph_preamble()

    ax.set_xlabel("Regular DPOR")
    ax.set_ylabel("Lazy DPOR")

    for (idx, name) in enumerate(benchmarks_sorted):
        bench = benchmarks[name]
        print name

        x = [bench.dpor_res.num_lhbgs, bench.dpor_res.num_scheds]
        y = [bench.ldpor_res.num_lhbgs, bench.ldpor_res.num_scheds]

        if x[0] == 0:
            x[0] = 1
        if y[0] == 0:
            y[0] = 1

        assert x[0] != 0
        assert y[0] != 0
        assert x[1] != 0
        assert y[1] != 0

        #a = ax.scatter(x[0], y[0], s=50, lw=1, edgecolor="k", facecolor='none', marker='x', alpha=1, clip_on=False)




        ax.annotate("",
                    xytext=(x[0], y[0]), textcoords='data',
                    xy=(x[1], y[1]), xycoords='data',
                    size=10,
                    arrowprops=dict(arrowstyle="->",
                                    linestyle='solid',
                                    connectionstyle="arc3,rad=0.1",
                                    lw=0.5, alpha=0.4)
                    )

        # a = ax.scatter(x[0], y[0],
        #                s=50, lw=1, edgecolor="k", facecolor='none', marker='x', alpha=1, clip_on=False)

        # a = ax.scatter(x[0], y[0], s=50, lw=0.5, edgecolor="k", facecolor='none', marker='o', alpha=0.5,
        #                clip_on=False)

        strfrm = r'$\textbf{{{0}}}$'
        if bench.ldpor_res.violation_detected:
            strfrm = r'$\underline{{\textbf{{{0}}}}}$'

        temp = ax.text(x[0], y[0], strfrm.format(idx + 1), size="7", ha="center", fontweight='700', alpha=1)
        temp.set_transform(temp.get_transform() + Affine2D().translate(0, -2.3)) #-2.3 for size 7, -1.8 for size 5




        b = ax.scatter(x[1], y[1],
                   s=10,
                   lw=0.5, edgecolor="k", facecolor='none', marker='s', alpha=0.5, clip_on=False)

        #temp = ax.text(x[0], y[0], " "+str(idx), size="7", ha="center", alpha=0.8)
        #temp.set_transform(temp.get_transform() + Affine2D().translate(-6, 3))
    #ms = MarkerStyle(marker='x').get_alt_transform()
    #ms = Rectangle((0, 0), 1, 1, fc="r")

    #Legend.update_default_handler_map({Text : HandlerText})
    # ax2 = ax.twinx()
    # ax2.set_xscale('linear')
    # ax2.set_yscale('linear')
    # ax.plot(range(1, 1000), 'k', label=r'\makebox[25]{1\hfill}Bla')
    # ax.plot(range(1, 1100), 'k', label=r'\makebox[25]{12\hfill}Bla12')
    # lgd = ax.legend(handlelength=-0.4)
    # for k in lgd.get_lines():
    #     k.set_linewidth(0)

    l = ax.legend([temp,b], [r'\#lazy HBRs', '\#terminal executions'], scatterpoints=1, loc=4, fontsize='small', fancybox=True, handler_map={Text : HandlerText})
    text = Text(text=r'$n$', x=8)
    text.set_figure(fig)
    c = l.get_children()[0].get_children()[1].get_children()[0]
    c.get_children()[0].get_children()[0].add_artist(text)

    # l.update_default_handler_map()
    # Legend.legendHandles

    fig.savefig(filename, bbox_inches=0)

def graph_lcaching_vs_caching(filename, benchmarks_sorted, benchmarks):
    """
    :type benchmarks_sorted: list of str
    :type benchmarks: dict[str, MyResult]

    :param filename:
    :param benchmarks_sorted:
    """
    fig, ax = log_graph_preamble()

    ax.set_xlabel("HBR caching (\#lazy HBRs)")
    ax.set_ylabel("Lazy HBR caching (\#lazy HBRs)")

    for (idx, name) in enumerate(benchmarks_sorted):
        if name in hbr_caching_ignored:
            continue
        bench = benchmarks[name]

        print name

        x = [bench.hbg_res.num_lhbgs, bench.hbg_res.num_scheds]
        y = [bench.lhbg_res.num_lhbgs, bench.lhbg_res.num_scheds]

        if x[0] == 0:
            x[0] = 1
        if y[0] == 0:
            y[0] = 1

        assert x[0] != 0
        assert y[0] != 0
        assert x[1] != 0
        assert y[1] != 0

        #a = ax.scatter(x[0], y[0], s=50, lw=1, edgecolor="k", facecolor='none', marker='x', alpha=1, clip_on=False)




        # ax.annotate("",
        #             xytext=(x[0], y[0]), textcoords='data',
        #             xy=(x[1], y[1]), xycoords='data',
        #             size=10,
        #             arrowprops=dict(arrowstyle="->",
        #                             linestyle='solid',
        #                             connectionstyle="arc3,rad=0.1",
        #                             lw=0.5, alpha=0.4)
        #             )

        # a = ax.scatter(x[0], y[0],
        #                s=50, lw=1, edgecolor="k", facecolor='none', marker='x', alpha=1, clip_on=False)

        # a = ax.scatter(x[0], y[0], s=50, lw=0.5, edgecolor="k", facecolor='none', marker='o', alpha=0.5,
        #                clip_on=False)

        strfrm = r'$\textbf{{{0}}}$'
        # if bench.ldpor_res.num_scheds == sched_limit or bench.ldpor_res.saw_may_mutex_deadlock:
        #     strfrm = r'$\underline{{\textbf{{{0}}}}}$'

        temp = ax.text(x[0], y[0], strfrm.format(idx + 1), size="7", ha="center", fontweight='700', alpha=1)
        temp.set_transform(temp.get_transform() + Affine2D().translate(0, -2.3)) #-2.3 for size 7, -1.8 for size 5




        # b = ax.scatter(x[1], y[1],
        #            s=10,
        #            lw=0.5, edgecolor="k", facecolor='none', marker='s', alpha=0.5, clip_on=False)

        #temp = ax.text(x[0], y[0], " "+str(idx), size="7", ha="center", alpha=0.8)
        #temp.set_transform(temp.get_transform() + Affine2D().translate(-6, 3))
    #ms = MarkerStyle(marker='x').get_alt_transform()
    #ms = Rectangle((0, 0), 1, 1, fc="r")

    #Legend.update_default_handler_map({Text : HandlerText})
    # ax2 = ax.twinx()
    # ax2.set_xscale('linear')
    # ax2.set_yscale('linear')
    # ax.plot(range(1, 1000), 'k', label=r'\makebox[25]{1\hfill}Bla')
    # ax.plot(range(1, 1100), 'k', label=r'\makebox[25]{12\hfill}Bla12')
    # lgd = ax.legend(handlelength=-0.4)
    # for k in lgd.get_lines():
    #     k.set_linewidth(0)

    # l = ax.legend([temp,b], [r'\#lhbr', '\#e'], scatterpoints=1, loc=4, fontsize='small', fancybox=True, handler_map={Text : HandlerText})
    # text = Text(text=r'$n$', x=8)
    # text.set_figure(fig)
    # c = l.get_children()[0].get_children()[1].get_children()[0]
    # c.get_children()[0].get_children()[0].add_artist(text)

    # l.update_default_handler_map()
    # Legend.legendHandles

    fig.savefig(filename, bbox_inches=0, transparent=True)



def graph_ldpor_vs_dpor_time(filename, benchmarks_sorted, benchmarks):
    """
    :type benchmarks_sorted: list of str
    :type benchmarks: dict[str, MyResult]

    :param filename:
    :param benchmarks_sorted:
    """
    fig, ax = log_graph_preamble()

    ax.set_xlabel("Regular DPOR")
    ax.set_ylabel("Lazy DPOR")

    for (idx, name) in enumerate(benchmarks_sorted):
        bench = benchmarks[name]
        print name

        x = [bench.dpor_res.num_lhbgs, bench.dpor_res.time*10]
        y = [bench.ldpor_res.num_lhbgs, bench.ldpor_res.time*10]

        if x[0] == 0:
            x[0] = 1
        if y[0] == 0:
            y[0] = 1
        if x[1] == 0:
            x[1] = 1
        if y[1] == 0:
            y[1] = 1

        assert x[0] != 0
        assert y[0] != 0
        assert x[1] != 0
        assert y[1] != 0

        #a = ax.scatter(x[0], y[0], s=50, lw=1, edgecolor="k", facecolor='none', marker='x', alpha=1, clip_on=False)




        ax.annotate("",
                    xytext=(x[0], y[0]), textcoords='data',
                    xy=(x[1], y[1]), xycoords='data',
                    size=10,
                    arrowprops=dict(arrowstyle="->",
                                    linestyle='solid',
                                    connectionstyle="arc3,rad=0.1",
                                    lw=0.5, alpha=0.4)
                    )

        # a = ax.scatter(x[0], y[0],
        #                s=50, lw=1, edgecolor="k", facecolor='none', marker='x', alpha=1, clip_on=False)

        # a = ax.scatter(x[0], y[0], s=50, lw=0.5, edgecolor="k", facecolor='none', marker='o', alpha=0.5,
        #                clip_on=False)

        strfrm = r'$\textbf{{{0}}}$'
        if bench.ldpor_res.violation_detected:
            strfrm = r'$\underline{{\textbf{{{0}}}}}$'

        temp = ax.text(x[0], y[0], strfrm.format(idx + 1), size="7", ha="center", fontweight='700', alpha=1)
        temp.set_transform(temp.get_transform() + Affine2D().translate(0, -2.3)) #-2.3 for size 7, -1.8 for size 5




        b = ax.scatter(x[1], y[1],
                   s=10,
                   lw=0.5, edgecolor="k", facecolor='none', marker='s', alpha=0.5, clip_on=False)

        # temp = ax.text(x[0], y[0], " "+str(idx), size="7", ha="center", alpha=0.8)
        #temp.set_transform(temp.get_transform() + Affine2D().translate(-6, 3))
    #ms = MarkerStyle(marker='x').get_alt_transform()
    #ms = Rectangle((0, 0), 1, 1, fc="r")

    #Legend.update_default_handler_map({Text : HandlerText})
    l = ax.legend([temp,b], ['\#lazy HBRs', 'time in deciseconds'], scatterpoints=1, loc=4, fontsize='small', fancybox=True, handler_map={Text : HandlerText})
    text = Text(text=r'$n$', x=8)
    text.set_figure(fig)
    c = l.get_children()[0].get_children()[1].get_children()[0]
    c.get_children()[0].get_children()[0].add_artist(text)

    # l.update_default_handler_map()
    # Legend.legendHandles
    fig.savefig(filename, bbox_inches=0)

from matplotlib.legend_handler import HandlerBase


class HandlerText(HandlerBase):
    def __init__(self, a, b, c, d, **kw):
        HandlerBase.__init__(self, **kw)


    def create_artists(self, legend, orig_handle,
                       xdescent, ydescent, width, height, fontsize,
                       trans):
        return [Text(text='a')]


def graph_bugs(filename, benchmarks_sorted, benchmarks):
    """
    :type benchmarks_sorted: list of str
    :type benchmarks: dict[str, MyResult]

    :param filename:
    :param benchmarks_sorted:
    """
    fig, ax = log_graph_preamble()

    ax.set_xlabel("DPOR (\#terminal executions until bug found)")
    ax.set_ylabel("Lazy DPOR (\#terminal executions until bug found)")

    never = sched_limit

    for (idx, name) in enumerate(benchmarks_sorted):
        bench = benchmarks[name]

        x = [bench.dpor_res.first_error]
        y = [bench.ldpor_res.first_error]

        if x[0] == -1 and y[0] == -1:
            continue

        assert x[0] != 0
        assert y[0] != 0

        do_circle = False

        if x[0] == -1:
            x[0] = never
        if y[0] == -1:
            y[0] = bench.ldpor_res.num_scheds
            do_circle = True


        #m = 'o' if bench.dpor_res.num_scheds < sched_limit else 'x'

        if do_circle:
            a = ax.scatter(x[0], y[0], s=200, lw=0.5, edgecolor="k", facecolor='none', marker='o', alpha=1, clip_on=False)

        strfrm = r'$\textbf{{{0}}}$'
        if bench.ldpor_res.violation_detected:
            strfrm = r'$\underline{{\textbf{{{0}}}}}$'
            # a = ax.scatter(x[0], y[0], s=50, lw=0.5, edgecolor="k", facecolor='none', marker='o', alpha=0.5, clip_on=False)

        temp = ax.text(x[0], y[0], strfrm.format(idx+1), size="7", ha="center", fontweight='700', alpha=1)
        temp.set_transform(temp.get_transform() + Affine2D().translate(0, -3)) #-2.3 for size 7, -1.8 for size 5

        #temp = ax.text(x[0], y[0], " "+str(name), size="2", ha="center", alpha=0.8)
        #temp.set_transform(temp.get_transform() + Affine2D().translate(0, 4))

    fig.savefig(filename, bbox_inches=0)


def graph_cumulative(filename, benchmarks_sorted, benchmarks):
    """
    :type benchmarks_sorted: list of str
    :type benchmarks: dict[str, MyResult]

    :param filename:
    :param benchmarks_sorted:
    """
    fig, ax = c_graph_preamble()

    ax.set_xlabel("")
    ax.set_ylabel("")

    cumulative = [0 for i in range(0, sched_limit + 1)]
    cumulative_l = [0 for i in range(0, sched_limit+1)]
    for name in benchmarks_sorted:
        bench = benchmarks[name]
        for i, c in enumerate(bench.dpor_res.cumulative_num_hbgs):
            cumulative[i] += c
        for i, c in enumerate(bench.ldpor_res.cumulative_num_hbgs):
            cumulative_l[i] += c

    x = [i for i in range(0, sched_limit + 1)]

    cumulative[0] = 1
    cumulative_l[0] = 1

    plt.plot(x, cumulative, c='blue')

    # temp = ax.text(x[0], y[0], " "+str(idx), size="7", ha="center", alpha=0.8)
    #temp.set_transform(temp.get_transform() + Affine2D().translate(-6, 3))

    fig.savefig(filename, bbox_inches=0)

def graph_cumulative_verified(filename, benchmarks):
    """
    :type benchmarks: dict[str, MyResult]

    :param filename:
    :param benchmarks_sorted:
    """
    # sorted_bench = sorted(benchmarks.values(), key=lambda x : x.dpor_res.num_scheds)
    #
    # sorted_bench = map(lambda x : x.dpor_res , sorted_bench)

    plot_x = []
    counter=0
    i=1
    while i <= sched_limit:
        plot_x.append(i)
        counter += 0.25
        i = pow(10, counter)

    dpor_plot_num = []
    for i in plot_x:
        counter = 0
        for b in benchmarks.values():
            if b.dpor_verified() and b.dpor_res.num_scheds <= i:
                counter += 1
        dpor_plot_num.append(counter)

    ldpor_plot_num = []
    for i in plot_x:
        counter = 0
        for b in benchmarks.values():
            if b.ldpor_verified() and b.ldpor_res.num_scheds <= i:
                counter += 1
        ldpor_plot_num.append(counter)

    ldpor_dpor_plot_num = []
    for i in plot_x:
        counter = 0
        for b in benchmarks.values():
            if (b.ldpor_verified() and b.ldpor_res.num_scheds <= i) or (b.dpor_verified() and b.ldpor_res.violation_detected and b.dpor_res.num_scheds <= i):
                counter += 1
        ldpor_dpor_plot_num.append(counter)


# count = 0
#     for benchmark in sorted_bench:
#         benchmark = benchmark
#         ':type: CommonResult'
#         if benchmark.num_scheds < sched_limit:
#             count += 1
#             dpor_plot_sched.append(benchmark.num_scheds)
#             dpor_plot_num.append(count)
#
#     sorted_bench = sorted(benchmarks.values(), key=lambda x: x.ldpor_res.num_scheds)
#     sorted_bench = map(lambda x: x.ldpor_res, sorted_bench)
#     ldpor_plot_sched = []
#     ldpor_plot_num = []
#
#     count = 0
#     for benchmark in sorted_bench:
#         benchmark = benchmark
#         ':type: CommonResult'
#         if benchmark.num_scheds < sched_limit and not benchmark.saw_may_mutex_deadlock:
#             count += 1
#             ldpor_plot_sched.append(benchmark.num_scheds)
#             ldpor_plot_num.append(count)

    fig, ax = c_graph_preamble()

    ax.set_xlabel("\#e")
    ax.set_ylabel("\#verified")

    plt.plot(plot_x, ldpor_dpor_plot_num, ':', c='k')
    plt.plot(plot_x, dpor_plot_num, '-', c='green')

    plt.plot(plot_x, ldpor_plot_num, '--', c='blue')


    plt.legend((r'LDPOR*', r'DPOR', r'LDPOR'), loc=4, fontsize='small', fancybox=True)

    fig.savefig(filename, bbox_inches=0)

# def graph_top_dpor_red(filename, benchmarks):
#     """
#         :type benchmarks: dict[str, MyResult]
#     """
#     benchmarks_list = benchmarks.values()
#     sorted(benchmarks_list, )


def output_macros(filename, benchmarks_sorted, benchmarks):
    """
       :type benchmarks_sorted: list of str
       :type benchmarks: dict[str, MyResult]
    """
    count = 0

    with open(filename, 'w') as fh:
        def write_macro(s1,s2):
            if isinstance(s2, int):
                s2 = "{:,d}".format(s2)
            s2 = str(s2)
            fh.write("\\newcommand{\\"+s1+"}{"+s2+"}\n")



        verified_dpor_set = set()
        for name in benchmarks_sorted:
            bench = benchmarks[name]
            ':type: MyResult'
            if bench.dpor_verified():
                verified_dpor_set.add(name)

        verified_ldpor_set = set()
        for name in benchmarks_sorted:
            bench = benchmarks[name]
            ':type: MyResult'
            if bench.ldpor_verified():
                verified_ldpor_set.add(name)

        predicted_benefit = set()

        count = 0
        for name in benchmarks_sorted:
            bench = benchmarks[name]
            if bench.dpor_res.num_lhbgs < bench.dpor_res.num_hbgs:
                count += 1
                predicted_benefit.add(name)
        write_macro("dporNumLhbLtHb", count)

        count = 0
        for name in benchmarks_sorted:
            bench = benchmarks[name]
            if bench.dpor_res.num_lhbgs < bench.dpor_res.num_hbgs and bench.dpor_res.num_scheds < sched_limit:
                count += 1
        write_macro("dporNumLhbLtHbAndComplete", count)

        count = 0
        for name in benchmarks_sorted:
            bench = benchmarks[name]
            if bench.dpor_res.num_lhbgs == bench.dpor_res.num_hbgs:
                count += 1
        write_macro("dporNumLhbEqHb", count)

        count = 0
        for name in benchmarks_sorted:
            bench = benchmarks[name]
            if bench.dpor_res.num_lhbgs == bench.dpor_res.num_hbgs and bench.dpor_res.num_lock == 0:
                count += 1
        write_macro("dporNumLhbEqHbAndNoLock", count)

        count = 0
        for name in benchmarks_sorted:
            bench = benchmarks[name]
            if bench.dpor_res.num_lhbgs == bench.dpor_res.num_hbgs and bench.dpor_res.num_lock > 0:
                count += 1
        write_macro("dporNumLhbEqHbAndLocks", count)

        count = 0
        for name in benchmarks_sorted:
            bench = benchmarks[name]
            if bench.dpor_res.num_lhbgs == bench.dpor_res.num_hbgs and bench.dpor_res.num_lock > 0 and bench.dpor_res.num_scheds == sched_limit:
                count += 1
        write_macro("dporNumLhbEqHbAndLocksAndIncomplete", count)

        count = 0
        for name in benchmarks_sorted:
            count += 1
        write_macro("numBenchmarks", count)

        count1 = 0
        for name in benchmarks_sorted:
            bench = benchmarks[name]
            count1 += bench.dpor_res.num_hbgs
        write_macro("totalHBGsDPOR", count1)

        count2 = 0
        for name in benchmarks_sorted:
            bench = benchmarks[name]
            count2 += bench.dpor_res.num_lhbgs
        write_macro("totalLHBGsDPOR", count2)


        count1 = 0
        for name in predicted_benefit:
            bench = benchmarks[name]
            count1 += bench.dpor_res.num_hbgs
        write_macro("totalHBGsDPORpredicted", count1)

        count2 = 0
        for name in predicted_benefit:
            bench = benchmarks[name]
            count2 += bench.dpor_res.num_lhbgs
        write_macro("totalLHBGsDPORpredicted", count2)

        write_macro("percDPORredundantHBRsPredicted", (count1-count2)*100 / count1)
        write_macro("totalDPORredundantHBRsPredicted", (count1 - count2))

        count1 = 0
        for name in benchmarks_sorted:
            bench = benchmarks[name]
            if bench.dpor_res.num_lock > 0:
                count1 += bench.dpor_res.num_hbgs
        write_macro("totalHBGsDPORwithLocks", count1)

        count2 = 0
        for name in benchmarks_sorted:
            bench = benchmarks[name]
            if bench.dpor_res.num_lock > 0:
                count2 += bench.dpor_res.num_lhbgs
        write_macro("totalLHBGsDPORwithLocks", count2)

        write_macro("percDPORredundantHBRsWithLocks", (count1-count2)*100 / count1)

        count = 0
        very_red = 70
        extreme_benchmarks = set()
        for name in predicted_benefit:
            bench = benchmarks[name]
            hbr = bench.dpor_res.num_hbgs
            lhbr = bench.dpor_res.num_lhbgs
            diff = hbr - lhbr
            perc_red = diff*100/hbr
            if perc_red >= very_red:
                count += 1
                extreme_benchmarks.add(name)
        write_macro("dporNumManyRedPredicted", count)
        write_macro("ManyRedPercPredicted", very_red)

        low_bound = 100

        write_macro("dporSchedsLowBound", low_bound)

        count1 = 0
        for name in predicted_benefit:
            bench = benchmarks[name]
            if name not in extreme_benchmarks:# and bench.dpor_res.num_scheds >= low_bound:
                count1 += bench.dpor_res.num_hbgs
        write_macro("totalHBGsDPORIgnoringExtremePredicted", count1)

        count2 = 0
        for name in predicted_benefit:
            bench = benchmarks[name]
            if name not in extreme_benchmarks:# and bench.dpor_res.num_scheds >= low_bound:
                count2 += bench.dpor_res.num_lhbgs
        write_macro("totalLHBGsDPORIgnoringExtremePredicted", count2)

        write_macro("percDPORredundantHBRsIgnoringExtremePredicted", (count1 - count2) * 100 / count1)
        write_macro("totalDPORredundantHBRsIgnoringExtremePredicted", (count1 - count2))

        count = 0
        for name in benchmarks_sorted:
            bench = benchmarks[name]
            if bench.dpor_res.num_scheds < low_bound:
                count += 1
        write_macro("numLessThanLowBoundSchedsDPOR", count)

        count = 0
        for name in benchmarks_sorted:
            bench = benchmarks[name]
            ':type: MyResult'
            if bench in verified_dpor_set and bench not in verified_ldpor_set and bench.dpor_res.num_lhbgs < bench.ldpor_res.num_lhbgs:
                count += 1
        write_macro("numVerDporUnkLdporMoreLhbrDpor", count)

        wordsDict = {'0': 'Zero',
                     '1': 'One', '2': 'Two', '3': 'Three', '4': 'Four', '5': 'Five', '6': 'Six', '7': 'Seven',
                     '8': 'Eight', '9': 'Nine', '-' : ''}

        def multiple_replace(text, wordDict):
            for key in wordDict:
                text = text.replace(key, wordDict[key])
            return text

        count = 0
        for (idx, name) in enumerate(benchmarks_sorted):
            name = multiple_replace(name, wordsDict)
            write_macro("bench"+name, idx+1)

        bench = benchmarks["cache4j-2t-2"]
        write_macro("cacheForjStatesDpor", bench.dpor_res.num_lhbgs)
        write_macro("cacheForjStatesLdpor", bench.ldpor_res.num_lhbgs)
        write_macro("cacheForjExLdpor", bench.ldpor_res.num_scheds)

        max_diff_amount = -100000
        max_diff_bench = None

        min_diff_amount = 100000
        min_diff_bench = None

        for (idx, name) in enumerate(benchmarks_sorted):
            bench = benchmarks[name]
            ':type: MyResult'
            if bench.dpor_res.time < 10 or bench.ldpor_res.time < 10 or (bench.ldpor_res.saw_may_mutex_deadlock and bench.ldpor_res.num_scheds < sched_limit):
                continue
            diff = (bench.dpor_res.scheds_per_second - bench.ldpor_res.scheds_per_second)*100/bench.dpor_res.scheds_per_second
            if diff > max_diff_amount:
                max_diff_amount = diff
                max_diff_bench = bench
            if diff < min_diff_amount:
                min_diff_amount = diff
                min_diff_bench = bench

        write_macro("maxDiffAmount", int(max_diff_amount))
        write_macro("minDiffAmount", int(min_diff_amount))
        write_macro("maxDiffBenchmark", max_diff_bench.name)
        write_macro("minDiffBenchmark", min_diff_bench.name)

        count = 0
        for name in benchmarks_sorted:
            bench = benchmarks[name]
            if bench.saw_mutex_deadlock() and not bench.ldpor_res.violation_detected:
                count += 1
        write_macro("numSawMutexDeadlockButNoViolation", count)

        count = 0
        for name in benchmarks_sorted:
            bench = benchmarks[name]
            if bench.saw_mutex_deadlock() and bench.ldpor_res.violation_detected:
                count += 1
        write_macro("numSawMutexDeadlockAndViolationDetected", count)

        count = 0
        for name in benchmarks_sorted:
            bench = benchmarks[name]
            if not bench.saw_mutex_deadlock() and bench.ldpor_res.violation_detected:
                count += 1
        write_macro("numNoMutexDeadlockButViolationDetected", count)

        max_execs_needed = -1
        max_execs_needed_bench = ""
        for name in benchmarks_sorted:
            bench = benchmarks[name]
            first_violation_detected = min(bench.ldpor_res.first_mutex_deadlock, bench.ldpor_res.first_may_mutex_deadlock)
            if first_violation_detected > max_execs_needed:
                max_execs_needed = first_violation_detected
                max_execs_needed_bench = name
        write_macro("maxExecsToFindViolation", max_execs_needed)
        write_macro("maxExecsToFindViolationName", max_execs_needed_bench)

        count = 0
        for name in benchmarks_sorted:
            bench = benchmarks[name]
            if bench.dpor_verified() and bench.dpor_res.num_scheds <= 100:
                count += 1
        write_macro("numDPORverifiedAfterHundred", count)

        count = 0
        for name in benchmarks_sorted:
            bench = benchmarks[name]
            if bench.ldpor_verified() and bench.ldpor_res.num_scheds <= 100:
                count += 1
        write_macro("numLDPORverifiedAfterHundred", count)

        count = 0
        for name in benchmarks_sorted:
            bench = benchmarks[name]
            if (bench.ldpor_verified() and bench.ldpor_res.num_scheds <= 100) or (bench.dpor_verified() and bench.dpor_res.num_scheds <= 100 and bench.ldpor_res.violation_detected):
                count += 1
        write_macro("numLDPORstarVerifiedAfterHundred", count)



        #
        #
        #

        count = 0
        for name in benchmarks_sorted:
            bench = benchmarks[name]
            if bench.dpor_verified():
                count += 1
        write_macro("numDPORverified", count)

        count = 0
        for name in benchmarks_sorted:
            bench = benchmarks[name]
            if bench.ldpor_verified():
                count += 1
        write_macro("numLDPORverified", count)

        count = 0
        for name in benchmarks_sorted:
            bench = benchmarks[name]
            if bench.ldpor_verified() or (bench.dpor_verified() and bench.ldpor_res.violation_detected):
                count += 1
        write_macro("numLDPORstarVerified", count)

        write_macro("numNotVerified", len(benchmarks_sorted) - count)

        count = 0
        total_dpor = 0
        total_ldpor = 0
        for name in benchmarks_sorted:
            bench = benchmarks[name]
            if not (bench.ldpor_verified() or (bench.dpor_verified() and bench.ldpor_res.violation_detected)):
                count += (bench.ldpor_res.num_lhbgs - bench.dpor_res.num_lhbgs)
                total_dpor += bench.dpor_res.num_lhbgs
                total_ldpor += bench.ldpor_res.num_lhbgs

        write_macro("numLhbrMoreThanDporForNotVerified", count)
        write_macro("numLhbrPercMoreThanDporForNotVerified", (total_ldpor - total_dpor)*100/ total_dpor)

        count = 0
        for name in benchmarks_sorted:
            bench = benchmarks[name]
            if bench.ldpor_res.first_error != -1 or bench.dpor_res.first_error != -1:
                count += 1
        write_macro("numBugsFound", count)

        count = 0
        for name in benchmarks_sorted:
            bench = benchmarks[name]
            if bench.ldpor_res.first_error == -1 and bench.dpor_res.first_error != -1:
                count += 1
        write_macro("numBugsMissedByLdpor", count)

        count = 0
        for name in benchmarks_sorted:
            bench = benchmarks[name]
            if (bench.ldpor_res.first_error != -1 or bench.dpor_res.first_error != -1) and bench.ldpor_res.first_error < bench.dpor_res.first_error:
                count += 1
        write_macro("numBugsFoundByLdporFirst", count)

        count = 0
        for name in benchmarks_sorted:
            bench = benchmarks[name]
            if (bench.ldpor_res.first_error != -1 or bench.dpor_res.first_error != -1) and bench.ldpor_res.first_error > bench.dpor_res.first_error:
                count += 1
        write_macro("numBugsFoundByDporFirstExcludingLdporMissed", count)

        count = 0
        for name in benchmarks_sorted:
            if name in hbr_caching_ignored:
                continue
            bench = benchmarks[name]
            if bench.hbg_res.num_scheds == sched_limit and bench.lhbg_res.num_scheds == sched_limit:
                count += 1
        write_macro("numHitExecutionLimitForBothHBRCandLHBRC", count)

        count = 0
        for name in benchmarks_sorted:
            if name in hbr_caching_ignored:
                continue
            bench = benchmarks[name]
            if bench.hbg_res.num_scheds == sched_limit and bench.lhbg_res.num_scheds < sched_limit:
                count += 1
        write_macro("numHitExecutionLimitForHBRCbutNotLHBRC", count)

        hbrc_benefited = set()
        count = 0
        for name in benchmarks_sorted:
            if name in hbr_caching_ignored:
                continue
            bench = benchmarks[name]
            if bench.hbg_res.num_lhbgs < bench.lhbg_res.num_lhbgs:
                count += 1
                hbrc_benefited.add(name)
        write_macro("numBenefitFromLHBRC", count)

        count = 0
        for name in benchmarks_sorted:
            if name in hbr_caching_ignored:
                continue
            bench = benchmarks[name]
            if bench.hbg_res.num_lhbgs > bench.lhbg_res.num_lhbgs:
                count += 1
        write_macro("numSufferFromLHBRC", count)

        total_normal = 0
        total_lazy = 0
        for name in hbrc_benefited:
            bench = benchmarks[name]
            total_normal += bench.hbg_res.num_lhbgs
            total_lazy += bench.lhbg_res.num_lhbgs

        write_macro("LHBRCIncreaseLHBRNum", total_lazy - total_normal)

        write_macro("LHBRCIncreaseLHBRPerc", (total_lazy - total_normal)*100/total_normal)

        total_normal = 0
        total_lazy = 0
        for name in benchmarks_sorted:
            bench = benchmarks[name]
            if bench.hbg_res == None or bench.lhbg_res == None:
                print "Missing HBG results for: " + name
            else:
                total_normal += bench.hbg_res.num_lhbgs
                total_lazy += bench.lhbg_res.num_lhbgs

        write_macro("LHBRCIncreaseTotal", total_lazy - total_normal)

        write_macro("LHBRCIncreaseTotalPerc", (total_lazy - total_normal)*100/total_normal)


        total_normal = 0
        total_lazy = 0
        for name in predicted_benefit:
            if name in hbr_caching_ignored:
                continue
            bench = benchmarks[name]
            total_normal += bench.hbg_res.num_lhbgs
            total_lazy += bench.lhbg_res.num_lhbgs

        write_macro("LHBRCIncreaseLHBRNumAcrossPredicted", total_lazy - total_normal)

        write_macro("LHBRCIncreaseLHBRPercAcrossPredicted", (total_lazy - total_normal) * 100 / total_normal)

        total_dpor = 0
        total_ldpor = 0
        for name in predicted_benefit:
            bench = benchmarks[name]
            total_dpor += bench.dpor_res.num_lhbgs
            total_ldpor += bench.ldpor_res.num_lhbgs

        write_macro("lazyDporIncreaseAcrossPredicted", total_ldpor - total_dpor)

        write_macro("lazyDporIncreasePercAcrossPredicted", (total_ldpor - total_dpor) * 100 / total_dpor)

        benchmarks_ldpor_benefit = set()
        count = 0
        for name in benchmarks_sorted:
            bench = benchmarks[name]
            if bench.ldpor_res.num_lhbgs >= bench.dpor_res.num_lhbgs and bench.ldpor_res.num_scheds <= bench.dpor_res.num_scheds and not (bench.ldpor_res.num_lhbgs == bench.dpor_res.num_lhbgs and bench.ldpor_res.num_scheds == bench.dpor_res.num_scheds):
                count += 1
                benchmarks_ldpor_benefit.add(name)
        write_macro("lazyDporNumBenefited", count)

        total_dpor = 0
        total_ldpor = 0
        for name in benchmarks_ldpor_benefit:
            bench = benchmarks[name]
            total_dpor += bench.dpor_res.num_lhbgs
            total_ldpor += bench.ldpor_res.num_lhbgs

        write_macro("lazyDporIncreaseAcrossBenefit", total_ldpor - total_dpor)

        write_macro("lazyDporIncreasePercAcrossBenefit", (total_ldpor - total_dpor) * 100 / total_dpor)

        total_dpor = 0
        total_ldpor = 0
        for name in benchmarks_sorted:
            bench = benchmarks[name]
            total_dpor += bench.dpor_res.num_lhbgs
            total_ldpor += bench.ldpor_res.num_lhbgs

        write_macro("lazyDporIncreaseAcrossAll", total_ldpor - total_dpor)

        write_macro("lazyDporIncreasePercAcrossAll", (total_ldpor - total_dpor) * 100 / total_dpor)



if __name__ == "__main__":
    def main():

        benchmarks = {}
        ':type: dict of (str, MyResult)'

        def get_benchmark(name, num_sched_points):
            try:
                return benchmarks[name]
            except KeyError:
                result = MyResult(name, num_sched_points)
                benchmarks[name] = result
                return result

        results_file = sys.argv[1]
        with open(results_file, 'r') as fh:
            for line in fh:
                if len(line.strip()) == 0:
                    continue
                line = line.split(",")
                line = [s.strip() for s in line]
                res = get_benchmark(line[0], int(line[14]))
                common_res = CommonResult(int(line[6]), int(line[4]), int(line[5]))
                ':type: CommonResult'
                common_res.percRedundantHBRs = (common_res.num_hbgs - common_res.num_lhbgs) * 100 / common_res.num_hbgs
                common_res.saw_may_mutex_deadlock = True if line[8] == 'true' else False
                common_res.num_lock = int(line[16])
                common_res.time = int(line[7])
                common_res.first_may_mutex_deadlock = int(line[22])
                common_res.first_mutex_deadlock = int(line[23])
                common_res.violation_detected = common_res.first_may_mutex_deadlock != -1 or common_res.first_mutex_deadlock != -1
                common_res.scheds_per_second = float(common_res.num_scheds) / float(max(common_res.time, 1))
                common_res.first_error = int(line[21])

                if line[1] == 'true':
                    res.ldpor_res = common_res
                else:
                    res.dpor_res = common_res

        results_file = sys.argv[2]
        with open(results_file, 'r') as fh:
            for line in fh:
                if len(line.strip()) == 0:
                    continue
                line = line.split(",")
                line = [s.strip() for s in line]
                res = get_benchmark(line[0], int(line[14]))
                common_res = CommonResult(int(line[6]), int(line[4]), int(line[5]))
                ':type: CommonResult'
                common_res.percRedundantHBRs = (common_res.num_hbgs - common_res.num_lhbgs) * 100 / common_res.num_hbgs
                #common_res.saw_may_mutex_deadlock = True if line[8] == 'true' else False
                #common_res.num_lock = int(line[16])
                common_res.time = int(line[7])
                #common_res.first_may_mutex_deadlock = int(line[22])
                #common_res.first_mutex_deadlock = int(line[23])
                #common_res.violation_detected = common_res.first_may_mutex_deadlock != -1 or common_res.first_mutex_deadlock != -1
                common_res.scheds_per_second = float(common_res.num_scheds) / float(max(common_res.time, 1))
                common_res.first_error = int(line[21])

                if res.hbg_res == None:
                    res.hbg_res = common_res
                else:
                    res.lhbg_res = common_res

        benchmarks_sorted = benchmarks.keys()
        benchmarks_sorted.sort()
        #graph_cumulative_verified('graph_cumulative_verified.pdf', benchmarks)
        output_macros("genmacros.tex", benchmarks_sorted, benchmarks)
        #graph_dpor("graph_dpor.pdf", benchmarks_sorted, benchmarks)
        #graph_ldpor_vs_dpor("graph_ldpor_vs_dpor.pdf", benchmarks_sorted, benchmarks)
        #graph_ldpor_vs_dpor_time("graph_ldpor_vs_dpor_time.pdf", benchmarks_sorted, benchmarks)
        #graph_bugs("graph_bugs.pdf", benchmarks_sorted, benchmarks)
        graph_lcaching_vs_caching("graph_hbrc.pdf", benchmarks_sorted, benchmarks)



        #read_logs('testdir', benchmarks, benchmarks_sorted)
        #output_table(benchmarks_sorted, benchmarks)
        #graph_cumulative("graph_cumulative.pdf", benchmarks_sorted, benchmarks)
    main()
