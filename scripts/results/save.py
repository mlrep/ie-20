import xlwt
from xlwt import Workbook
import math
import os


def create_color_style(color_name):
    style = xlwt.XFStyle()
    pattern = xlwt.Pattern()
    pattern.pattern = xlwt.Pattern.SOLID_PATTERN
    pattern.pattern_fore_colour = xlwt.Style.colour_map[color_name]
    style.pattern = pattern

    return style


COLOR_STYLES = {
    0:  create_color_style("red"),
    1:  create_color_style("yellow"),
    2:  create_color_style("green"),
}


def determine_col_colors(col_label_values, reverse):
    col_colors = {}
    col_label_values.sort(key=lambda x: list(x.values())[0], reverse=reverse)
    col_labels_sorted = list(map(lambda x: list(x.keys())[0], col_label_values))

    color_groups = len(COLOR_STYLES.keys())
    group_size = math.ceil((len(col_labels_sorted) / color_groups))

    for k in range(0, color_groups):
        for label in col_labels_sorted[k * group_size:(k + 1) * group_size]:
            col_colors[label] = k

    return col_colors


def write_stats_to_file(file_path, data, metrics, columns, subs, reverse_colors=False):
    book = Workbook()

    for metric in metrics:
        sheet = book.add_sheet(metric)
        [i, j] = [0, 0]

        for sub in subs:
            print("Writing {0} for: {1}".format(metric, sub))
            sheet.write(i, j, sub)

            colors = {}

            for col in columns:
                j += 1
                sheet.write(i, j, col)

                col_label_values = [{label: values[col][sub]} for label, values in data[metric].items()]
                colors[col] = determine_col_colors(col_label_values, reverse_colors)

            j = 0
            i += 1

            for label, results in data[metric].items():
                sheet.write(i, j, label)

                for col in columns:
                    j += 1
                    sheet.write(i, j, results[col][sub], COLOR_STYLES[colors[col][label]])

                i += 1
                j = 0

            i += 1

    book.save(file_path)


def write_latex_formulas_to_file(file_path, data, metrics, columns, subs, latex_out):
    book = Workbook()

    for metric in metrics:
        sheet = book.add_sheet(metric)
        [i, j] = [0, 0]

        for sub in subs:
            print("Writing latex series {0} for: {1}".format(metric, sub))
            sheet.write(i, j, sub)
            i += 1

            for label, results in data[metric].items():
                line = ""

                if latex_out == "SERIES":
                    line = concat_latex_series(label, results, columns, sub)
                elif latex_out == "TABLE":
                    line = concat_latex_table(label, results, columns, sub)

                sheet.write(i, j, line)
                i += 1

            i += 1

    book.save(file_path)


def concat_latex_series(label, results, columns, sub):
    line = label
    i = 0

    for col in columns:
        line += " ({0},{1})".format(i, results[col][sub])
        i += 1

    return line


def concat_latex_table(label, results, columns, sub):
    line = label

    for col in columns:
        line += " & {0}".format(results[col][sub])

    line += "\\\\"

    return line


def write_series_to_file(output_root_path, series_data):
    os.mkdir(output_root_path)

    for group_label, group_series in series_data.items():
        out = open("{0}/{1}.series".format(output_root_path, group_label), "w")

        for label, series in group_series.items():
            out.write("{0}".format(label))

            for i in range(len(series)):
                out.write(",{0}".format(series[i]))

            out.write("\n")

        out.close()


def write_ranks_to_file(file_path, ranks_data, metrics, columns, subs):
    book = Workbook()

    for metric in metrics:
        sheet = book.add_sheet(metric)
        rows = ranks_data[metric].keys()
        [i, j] = [0, 0]

        for sub in subs:
            print("Writing {0} for: {1}".format(metric, sub))
            sheet.write(i, j, sub)
            i += 1

            for col in columns:
                sheet.write(i, j, col)
                i += 1

                for row in rows:
                    sheet.write(i, j, row)
                    results = ranks_data[metric][row][col]

                    for rank_count in results[sub]:
                        j += 1
                        sheet.write(i, j, rank_count)

                    i += 1
                    j = 0

                i += 1
        i += 2

    book.save(file_path)


def write_merged_data_to_file(root_path, data, col_groups):
    for metric in data.keys():
        for group in col_groups:
            out_dir_path = "{0}/{1}/{2}".format(root_path, metric, group)
            os.makedirs(out_dir_path, exist_ok=True)
            out = open("{0}/all.csv".format(out_dir_path), "w")

            for row in data[metric].keys():
                values = ','.join(list(map(lambda x: str(x), data[metric][row][group])))
                out.write('{0},{1}\n'.format(row, values))

            out.close()
