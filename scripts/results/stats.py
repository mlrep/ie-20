import math
import numpy as np
import sys
import copy


"""
The data_stats result is built as [metric x row x column] and may contain sub results in each cell.
"""


def compute_avg_statistics(data_unit):
    data = data_unit["data"]
    data_stats = {}

    for metric in data.keys():
        data_stats[metric] = {}

        for row in data[metric].keys():
            data_stats[metric][row] = {}

            for col in data[metric][row].keys():
                print("Computing {0} for: {1} {2}".format(metric, row, col))

                data_stats[metric][row][col] = {}
                streams_results = data[metric][row][col]

                data_stats[metric][row][col] = avg_from_maps(streams_results, ["all"])

    for metric in data_stats.keys():
        for row in data_stats[metric].keys():
            data_stats[metric][row]["avg"] = avg_from_maps(data_stats[metric][row], ["all"])

    return data_stats


def avg_from_maps(map_data, subs):
    avg = {}

    for sub in subs:
        avg[sub] = 0

    for sub in subs:
        n = 0

        for s, sub_values in map_data.items():
            sv = sub_values[sub]
            if sv is not None and not (math.isnan(sv) or math.isinf(sv)):
                avg[sub] += sub_values[sub]
            else:
                n += 1

        avg[sub] /= (len(map_data.values()) - n)

    return avg


def compute_avg_from_series_statistics(data_unit):
    data = data_unit["data"]
    data_stats = {}

    for metric in data.keys():
        data_stats[metric] = {}

        for row in data[metric].keys():
            data_stats[metric][row] = {}

            for col in data[metric][row].keys():
                data_stats[metric][row][col] = {}
                streams_results = data[metric][row][col]
                print("Computing {0} for: {1} {2}".format(metric, row, col))

                for stream, series in streams_results.items():
                    streams_results[stream] = {"all": sum(series) / len(series)}

                data_stats[metric][row][col] = avg_from_maps(streams_results, ["all"])

    for metric in data_stats.keys():
        for row in data_stats[metric].keys():
            data_stats[metric][row]["avg"] = avg_from_maps(data_stats[metric][row], ["all"])

    return data_stats


def compute_series_drift_statistics(data_unit, subs, stream_defs, init_frac):
    data = data_unit["data"]
    data_stats = {}

    for metric in data.keys():
        data_stats[metric] = {}

        for row in data[metric].keys():
            data_stats[metric][row] = {}

            for col in data[metric][row].keys():
                data_stats[metric][row][col] = {}
                streams_results = data[metric][row][col]
                print("Computing {0} for: {1} {2}".format(metric, row, col))

                for stream, series in streams_results.items():
                    stream_def = stream_defs[stream]

                    if stream_def["drifts"] is not None:
                        streams_results[stream] = compute_defined_drift_statistics(series, stream_def, init_frac)
                    else:
                        streams_results[stream] = compute_undefined_drift_statistics(series, stream_def, init_frac)

                data_stats[metric][row][col] = avg_from_maps(streams_results, subs)

    for metric in data_stats.keys():
        for row in data_stats[metric].keys():
            data_stats[metric][row]["avg"] = avg_from_maps(data_stats[metric][row], subs)

    return data_stats


def compute_undefined_drift_statistics(data, drift_stream_defs, init_frac):
    data_stats = {"drift": None, "stable": None, "all": [], "avg": None}
    init_instances_num = int(init_frac * drift_stream_defs["size"])

    for i in range(init_instances_num, drift_stream_defs["size"], drift_stream_defs["log"]):
        j = int((i - init_instances_num) / drift_stream_defs["log"])
        data_stats["all"].append(data[j])

    data_stats["all"] = sum(data_stats["all"]) / len(data_stats["all"])

    return data_stats


def compute_defined_drift_statistics(data, drift_stream_defs, init_frac):
    data_stats = {"drift": [], "stable": [], "all": [], "avg": None}
    init_instances_num = int(init_frac * drift_stream_defs["size"])
    drifts = drift_stream_defs["drifts"]

    curr_drift = 0
    cd = drifts[curr_drift]
    drift_analysis_bound = int(drifts[curr_drift + 1]["p"] - drifts[curr_drift + 1]["w"] / 2.0)

    # print("{0} -> {1} (p = {2} w = {3})".format(cd["old"], cd["new"], cd["p"], cd["w"]))

    for i in range(init_instances_num, drift_stream_defs["size"], drift_stream_defs["log"]):
        drift_bound = int(cd["w"] / 2.0 if cd["w"] >= 20000 else 10000)
        is_drift = (i >= cd["p"] - (cd["w"] / 2.0)) and (i <= cd["p"] + drift_bound)

        if curr_drift < len(drifts) - 1 and i > drift_analysis_bound:
            curr_drift += 1
            cd = drifts[curr_drift]
            # print("{0} -> {1} (p = {2} w = {3})".format(cd["old"], cd["new"], cd["p"], cd["w"]))

            if curr_drift < len(drifts) - 1:
                drift_analysis_bound = int(drifts[curr_drift + 1]["p"] - drifts[curr_drift + 1]["w"] / 2.0)
            else:
                drift_analysis_bound = drift_stream_defs["size"]

        j = int((i - init_instances_num) / drift_stream_defs["log"])

        if is_drift:
            data_stats["drift"].append(data[j])
        else:
            data_stats["stable"].append(data[j])

        data_stats["all"].append(data[j])

    data_stats["stable"] = sum(data_stats["stable"]) / len(data_stats["stable"])
    data_stats["drift"] = sum(data_stats["drift"]) / len(data_stats["drift"])
    data_stats["all"] = sum(data_stats["all"]) / len(data_stats["all"])
    data_stats["avg"] = (data_stats["stable"] + data_stats["drift"]) / 2.0

    return data_stats


def compute_series_dissimilarity_statistics(data_unit, base_metrics, metrics, columns, fix_row, subs):
    data = data_unit["data"]
    data_stats = {}

    for base_metric in base_metrics:
        data_stats[base_metric] = {}
        specific_metrics = list(filter(lambda x: base_metric in x, metrics))

        for row in specific_metrics:
            data_stats[base_metric][row] = {}

            for col in data[row][fix_row].keys():
                data_stats[base_metric][row][col] = {}

                streams_base_results = data[base_metric + "_series"][fix_row][col]
                streams_results = data[row][fix_row][col]
                streams_stats = {}

                print("Computing base {0} similarity for: {1} {2}".format(base_metric, row, col))

                for stream, series in streams_results.items():
                    streams_stats[stream] = compute_series_dissimilarity(streams_base_results[stream], streams_results[stream])

                data_stats[base_metric][row][col] = avg_from_maps(streams_stats, subs)

    data_stats = normalize_data_stats(data_stats, base_metrics, metrics, columns, subs)

    return data_stats


def compute_series_dissimilarity(base_stream, stream):
    if len(base_stream) != len(stream):
        raise ValueError('Series are not equal! Got: {0} and {1}'.format(len(base_stream), len(stream)))

    return {"all": np.linalg.norm(np.asarray(base_stream) - np.asarray(stream))}


def normalize_data_stats(data_stats, metrics, rows, columns, subs):
    for metric in metrics:
        max_val = sys.float_info.min
        specific_rows = list(filter(lambda x: metric in x, rows))

        for row in specific_rows:
            for column in columns:
                for sub in subs:
                    val = data_stats[metric][row][column][sub]
                    if val > max_val:
                        max_val = val

        for row in specific_rows:
            for column in columns:
                for sub in subs:
                    data_stats[metric][row][column][sub] /= max_val

    return data_stats


def compute_replacements_statistics(data_unit):
    data = data_unit["data"]
    data_stats = {}

    for metric in data.keys():
        data_stats[metric] = {}

        for row in data[metric].keys():
            row = row.split("-")[0]
            data_stats[metric][row] = {}

            for col in data[metric][row].keys():
                data_stats[metric][row][col] = {}
                streams_results = data[metric][row][col]
                print("Computing {0} for: {1} {2}".format(metric, row, col))

                for stream, series in streams_results.items():
                    streams_results[stream] = {"all": series[-1]}

                data_stats[metric][row][col] = avg_from_maps(streams_results, ["all"])

    for metric in data_stats.keys():
        for row in data_stats[metric].keys():
            data_stats[metric][row]["avg"] = avg_from_maps(data_stats[metric][row], ["all"])

    return data_stats


def generate_replacement_ticks(data_unit):
    data = data_unit["data"]
    ticks = {}

    base_replacements_series_data = data["baseReplacements"]
    os_replacements_series_data = data["osReplacements"]

    for row in base_replacements_series_data.keys():
        for col in base_replacements_series_data[row].keys():
            base_streams_results = base_replacements_series_data[row][col]
            os_streams_results = os_replacements_series_data[row][col]
            print("Computing ticks for: {0} {1}".format(row, col))

            for stream in base_streams_results.keys():
                if stream not in ticks.keys():
                    ticks[stream] = {}

                base_replacements_series = base_streams_results[stream]
                os_replacements_series = os_streams_results[stream]
                n = len(base_replacements_series)
                ticks_series = [0]

                for i in range(1, n):
                    if base_replacements_series[i] > base_replacements_series[i - 1]:
                        ticks_series.append(1)
                    elif os_replacements_series[i] > os_replacements_series[i - 1]:
                        ticks_series.append(2)
                    else:
                        ticks_series.append(0)

                ticks[stream]["{0}-{1}".format(row, col)] = ticks_series

    return ticks


def extend_with_avg_metrics(data_stats):
    ext_data_stats = copy.deepcopy(data_stats)
    metrics_num = len(data_stats.keys())
    ext_data_stats["avg"] = {}

    for metric in data_stats.keys():

        for row in data_stats[metric].keys():
            if row not in ext_data_stats["avg"]:
                ext_data_stats["avg"][row] = {}

            for col in data_stats[metric][row].keys():
                if col not in ext_data_stats["avg"][row]:
                    ext_data_stats["avg"][row][col] = {}

                for sub in data_stats[metric][row][col].keys():
                    if sub not in ext_data_stats["avg"][row][col]:
                        ext_data_stats["avg"][row][col][sub] = 0

                    ext_data_stats["avg"][row][col][sub] += (data_stats[metric][row][col][sub] / metrics_num)

    return ext_data_stats


def compute_relative_statistics(data_stats, base_row):
    rel_data_stats = copy.deepcopy(data_stats)

    for metric in data_stats.keys():
        for row in data_stats[metric].keys():
            for col in data_stats[metric][row].keys():
                for sub in data_stats[metric][row][col].keys():
                    rel_data_stats[metric][row][col][sub] /= data_stats[metric][base_row][col][sub]

    return rel_data_stats


def compute_ranks_statistics(data_unit):
    data = data_unit["data"]
    cols = data_unit["columns"]
    data_stats = {}

    for metric in data.keys():
        data_stats[metric] = {}

        for col in cols:
            print("Computing ranks for: {0} {1}".format(metric, col))
            rows_ranks = compute_ranks(data[metric], col, data[metric].keys(), data_unit["streams"])

            for row, ranks in rows_ranks.items():
                if row not in data_stats[metric]:
                    data_stats[metric][row] = {}

                data_stats[metric][row][col] = {}
                data_stats[metric][row][col]["all"] = ranks

    return data_stats


def compute_ranks(data, col, rows, streams):
    ranks = {}

    for row in rows:
        ranks[row] = [0] * len(rows)

    for stream in streams:
        rows_results = []

        for row in rows:
            rows_results.append((row, data[row][col][stream]["all"]))

        sorted_rows_results = list(map(lambda rv: rv[0], sorted(rows_results, key=lambda rv: rv[1], reverse=True)))

        i = 0
        for row in sorted_rows_results:
            ranks[row][i] += 1
            i += 1

    return ranks


def merge_columns(data_unit, col_groups):
    data = data_unit['data']
    merged_data = {}

    for metric in data.keys():
        merged_data[metric] = {}

        for row in data[metric]:
            merged_data[metric][row] = {}

            for key, group in col_groups.items():
                merged_data[metric][row][key] = []

                for col in group:
                    for stream in data_unit['streams']:
                        merged_data[metric][row][key].append(data[metric][row][col][stream]['all'])

    return merged_data
