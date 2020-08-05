import numpy as np
from files import reader
import matplotlib as mpl
mpl.use('pgf')
import matplotlib.pyplot as plt
from matplotlib.ticker import FormatStrFormatter
from scipy.ndimage.filters import gaussian_filter1d
import math

green1 = '#004c00'
green2 = '#009900'
green3 = '#47da47'
green4 = '#99ea99'
blue1 = '#003566'
blue2 = '#186fc0'
blue3 = '#68aae7'
blue4 = '#a1caf0'
red1 = '#990000'
red2 = '#DD0000'
red3 = '#E74C4C'
red4 = '#F19999'


output_scale = 0.33
column_width = 243.911
text_width = 345.0


def figsize(scale):
    fig_width_pt = 1.15*text_width  # Get this from LaTeX using \the\textwidth (or column_width)
    inches_per_pt = 1.0 / 72.27  # Convert pt to inch
    golden_mean = (np.sqrt(5.0) - 1.0) / 2.0  # Aesthetic ratio (you could change this)
    fig_width = fig_width_pt * inches_per_pt * scale  # width in inches
    fig_height = fig_width * 0.7  # * golden_mean
    fig_size = [fig_width, fig_height]

    return fig_size


def init_pgf():
    pgf_with_latex = {
        "pgf.texsystem": "pdflatex",
        "text.usetex": True,
        "font.family": "serif",
        "font.serif": [],  # blank entries should cause plots to inherit fonts from the document
        "font.sans-serif": [],
        "font.monospace": [],
        "axes.labelsize": int(10),
        "font.size": int(10),
        "legend.fontsize": int(6),
        "xtick.labelsize": int(9),
        "ytick.labelsize": int(9),
        "axes.titlesize": int(11),
        "figure.figsize": figsize(output_scale),
        "pgf.preamble": [
            r"\usepackage{times}"
        ]
    }
    mpl.rcParams.update(pgf_with_latex)


def create_new_fig():
    plt.clf()
    fig = plt.figure()
    ax = fig.add_subplot(111)

    return fig, ax


def save_fig(filename):
    plt.savefig('{}.pdf'.format(filename), bbox_inches='tight', pad_inches=0.0)


def create_plot(window_width, intensity):
    y_se = [window_width] * intensity
    y_uw = [int(np.random.rand() * window_width) for _ in range(intensity)]
    y_ex = [int((1.0 - (-0.25*math.log(np.random.rand()) % 1)) * window_width) for _ in range(intensity)]
    print(y_se, y_uw)

    plt.hist(y_se, range=(0, 1001))
    plt.hist(y_uw, range=(0, 1001))
    plt.hist(y_ex, range=(0, 1001))
    save_fig('{0}'.format('strat_vis'))
