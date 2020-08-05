import os
import numpy as np


def get_dirs(path):
    return [name for name in os.listdir(path) if os.path.isdir("{0}/{1}".format(path, name))]


def get_base_metrics(metrics):
    return list(np.unique(list(map(lambda x: x.split("_")[0], metrics))))
