import utils
import math


def load_avg_data(roots, metrics, columns, roots_post=None, is_budget=False):
    streams = utils.get_dirs("{0}/{1}".format(roots[0], columns[0]))
    if roots_post is None:
        roots_post = [''] * len(roots)
    all = {}

    for metric in metrics:
        averages = {}

        for col in columns:
            for stream in streams:
                i = 0

                for root in roots:
                    data_path = "{0}/{1}/{2}/averages/{3}.data".format(root, col, stream, metric)
                    print("Reading: {0}".format(data_path))
                    data_file = open(data_path, "r")
                    data_lines = data_file.readlines()

                    for line in data_lines:
                        [row, value] = line.split(",")
                        if is_budget:
                            row = row.split("-")[0]

                        row += roots_post[i]

                        if row not in averages:
                            averages[row] = {}
                        if col not in averages[row]:
                            averages[row][col] = {}

                        averages[row][col][stream] = {"all": float(value.replace("\n", ""))}

                    i += 1

        all[metric] = averages

    return {
        "data": all,
        "streams": streams,
        "metrics": metrics,
        "columns": columns  # todo: add rows
    }


def load_series_data(roots, metrics, columns, roots_post=None, is_budget=False):
    streams = utils.get_dirs("{0}/{1}".format(roots[0], columns[0]))
    if roots_post is None:
        roots_post = [] * len(roots)
    all = {}

    for metric in metrics:
        series = {}

        for col in columns:
            for stream in streams:
                i = 0

                for root in roots:
                    data_path = "{0}/{1}/{2}/series/{3}.data".format(root, col, stream, metric)
                    print("Reading: {0}".format(data_path))
                    data_file = open(data_path, "r")
                    data_lines = data_file.readlines()

                    for line in data_lines:
                        [row, values] = line.split(",", 1)
                        if is_budget:
                            row = row.split("-")[0]

                        row += roots_post[i]

                        values = list(map(lambda x: float(x.replace("\n", "")), values.split(",")))
                        filter_values = list(filter(lambda x: not math.isnan(x), values))
                        avg = sum(filter_values) / len(filter_values)
                        values = list(map(lambda x: avg if math.isnan(x) else x, values))

                        if row not in series:
                            series[row] = {}
                        if col not in series[row]:
                            series[row][col] = {}

                        series[row][col][stream] = values

                    i += 1

        all[metric] = series

    return {
        "data": all,
        "metrics": metrics,
        "columns": columns,
        "streams": streams,
    }
