from os.path import expanduser
import load
import utils
import stats
import save
import defs
from pprint import pprint

HOME = expanduser("~")
INIT_INSTANCES = 0.05


def run_os_analysis():
    # run_os_parameters_analysis()
    # run_os_computing_time_analysis()
    # run_os_memory_analysis()
    # run_os_error_windows_analysis()
    # run_os_significance_perf_analysis()
    # run_os_significance_repls_analysis()
    run_os_final_analysis()
    run_final_ranks_analysis()
   # run_averages_extraction()


def run_os_parameters_analysis():
    roots = [
        "{0}/Results/os-synth/aht/alr".format(HOME),
        "{0}/Results/os-synth/sgd/alr".format(HOME),
        "{0}/Results/os-synth/aht/windows".format(HOME),
        "{0}/Results/os-synth/aht/windows-adapt".format(HOME),
        "{0}/Results/os-synth/aht/gens".format(HOME),
        "{0}/Results/os-synth/aht/gens-adapt".format(HOME),
        "{0}/Results/os-synth/sgd/windows".format(HOME),
        "{0}/Results/os-synth/sgd/windows-adapt".format(HOME),
        "{0}/Results/os-synth/sgd/gens".format(HOME),
        "{0}/Results/os-synth/sgd/gens-adapt".format(HOME)
    ]
    out_paths = [
        "aht_alr.xls",
        "sgd_alr.xls",
        "aht_windows.xls",
        "aht_windows_adapt.xls",
        "aht_gens.xls",
        "aht_gens_adapt.xls",
        "sgd_windows.xls",
        "sgd_windows_adapt.xls",
        "sgd_gens.xls",
        "sgd_gens_adapt.xls"
    ]

    latex_series_out_paths = list(map(lambda x: "latex_series_" + x, out_paths))
    latex_tables_out_paths = list(map(lambda x: "latex_tables_" + x, out_paths))

    columns = ["1.0", "0.5", "0.2", "0.1", "0.05", "0.01"]
    metrics = ["accuracy_series", "kappa_series"]
    subs = ["drift", "stable", "all", "avg"]

    for i in range(0, len(roots)):
        data = load.load_series_data([roots[i]], metrics, columns, True)

        data_stats = stats.compute_series_drift_statistics(data, subs, defs.DRIFT_STREAM_DEFS, INIT_INSTANCES)
        data_stats = stats.extend_with_avg_metrics(data_stats)
        save.write_stats_to_file(out_paths[i], data_stats, metrics + ["avg"], columns + ["avg"], subs)
        save.write_latex_formulas_to_file(latex_tables_out_paths[i], data_stats, metrics + ["avg"], columns, subs, "TABLE")
        save.write_latex_formulas_to_file(latex_series_out_paths[i], data_stats, metrics + ["avg"], columns, subs, "SERIES")

        data_stats_rel = stats.compute_relative_statistics(data_stats, "OS#ALR")
        data_stats_rel = stats.extend_with_avg_metrics(data_stats_rel)
        save.write_stats_to_file("rel_" + out_paths[i], data_stats_rel, metrics + ["avg"], columns + ["avg"], subs)
        save.write_latex_formulas_to_file("rel_" + latex_tables_out_paths[i], data_stats_rel, metrics + ["avg"], columns, subs, "TABLE")
        save.write_latex_formulas_to_file("rel_" + latex_series_out_paths[i], data_stats_rel, metrics + ["avg"], columns, subs, "SERIES")


def run_os_computing_time_analysis():
    roots = [
        "{0}/Results/os-synth/aht/gens".format(HOME),
        "{0}/Results/os-synth/aht/gens-adapt".format(HOME),
        "{0}/Results/os-synth/sgd/gens".format(HOME),
        "{0}/Results/os-synth/sgd/gens-adapt".format(HOME)
    ]
    out_paths = [
        "aht_gens_time.xls",
        "aht_gens_adapt_time.xls",
        "sgd_gens_time.xls",
        "sgd_gens_adapt_time.xls"
    ]

    columns = ["0.5", "0.2", "0.1", "0.05", "0.01"]
    metrics = ["kappa", "update_time", "classification_time"]
    subs = ["all"]

    for i in range(0, len(roots)):
        data = load.load_avg_data([roots[i]], metrics, columns, False)
        data_stats = stats.compute_avg_statistics(data)
        save.write_stats_to_file(out_paths[i], data_stats, metrics, columns + ["avg"], subs)


def run_os_memory_analysis():
    roots = [
        "{0}/Results/os-synth/aht/windows".format(HOME),
        "{0}/Results/os-synth/aht/windows-adapt".format(HOME),
        "{0}/Results/os-synth/sgd/windows".format(HOME),
        "{0}/Results/os-synth/sgd/windows-adapt".format(HOME),
    ]
    out_paths = [
        "aht_windows_mem.xls",
        "aht_windows_adapt_mem.xls",
        "sgd_windows_mem.xls",
        "sgd_windows_adapt_mem.xls",
    ]

    columns = ["0.5", "0.2", "0.1", "0.05", "0.01"]
    metrics = ["kappa_series", "windowSize"]
    subs = ["all"]

    for i in range(0, len(roots)):
        data = load.load_series_data([roots[i]], metrics, columns, False)
        data_stats = stats.compute_avg_from_series_statistics(data)
        save.write_stats_to_file(out_paths[i], data_stats, metrics, columns + ["avg"], subs)


def run_os_error_windows_analysis():
    roots = [
        "{0}/Results/os-synth/aht/error-windows".format(HOME),
        "{0}/Results/os-synth/sgd/error-windows".format(HOME),
    ]

    out_paths = [
        "aht_error_windows_synth.xls",
        "sgd_error_windows_synth.xls",
    ]

    latex_tables_out_paths = list(map(lambda x: "latex_tables_" + x, out_paths))

    metrics = ["accuracy_series", "accuracy_series_10", "accuracy_series_100", "accuracy_series_1000", "accuracy_series_10000",
               "accuracy_series_adwin", "accuracy_series_adwin_001", "accuracy_series_adwin_005", "accuracy_series_adwin_01", "accuracy_series_adwin_02",
               "kappa_series", "kappa_series_10", "kappa_series_100", "kappa_series_1000", "kappa_series_10000",
               "kappa_series_adwin", "kappa_series_adwin_001", "kappa_series_adwin_005", "kappa_series_adwin_01", "kappa_series_adwin_02"]
    columns = ["1.0", "0.5", "0.2", "0.1", "0.05", "0.01"]
    subs = ["all"]
    base_metrics = utils.get_base_metrics(metrics)

    for i in range(0, len(roots)):
        data = load.load_series_data([roots[i]], metrics, columns, False)
        data_stats = stats.compute_series_dissimilarity_statistics(data, base_metrics, metrics, columns, "OS#ALR", subs)
        save.write_stats_to_file(out_paths[i], data_stats, base_metrics, columns, subs, True)
        save.write_latex_formulas_to_file(latex_tables_out_paths[i], data_stats, base_metrics, columns, subs, "TABLE")


def run_os_significance_perf_analysis():
    roots = [
        "{0}/Results/os-synth/aht/ens-sign/ens-sign-se".format(HOME),
        "{0}/Results/os-synth/aht/ens-sign/ens-sign-pew".format(HOME),
        "{0}/Results/os-synth/sgd/ens-sign/ens-sign-se".format(HOME),
        "{0}/Results/os-synth/sgd/ens-sign/ens-sign-pew".format(HOME)
    ]

    out_paths = [
        "aht_ens_sign_se_perf_synth.xls",
        "aht_ens_sign_pew_perf_synth.xls",
        "sgd_ens_sign_se_perf_synth.xls",
        "sgd_ens_sign_pew_perf_synth.xls",
    ]

    latex_series_out_paths = list(map(lambda x: "latex_series_" + x, out_paths))
    latex_tables_out_paths = list(map(lambda x: "latex_tables_" + x, out_paths))

    metrics = ["accuracy_series", "kappa_series"]
    columns = ["1.0", "0.5", "0.2", "0.1", "0.05", "0.01"]
    subs = ["drift", "stable", "all"]

    for i in range(0, len(roots)):
        data = load.load_series_data([roots[i]], metrics, columns, True)

        data_stats = stats.compute_series_drift_statistics(data, subs, defs.DRIFT_STREAM_DEFS, INIT_INSTANCES)
        save.write_stats_to_file(out_paths[i], data_stats, metrics, columns + ["avg"], subs)
        save.write_latex_formulas_to_file(latex_tables_out_paths[i], data_stats, metrics, columns, subs, "TABLE")
        save.write_latex_formulas_to_file(latex_series_out_paths[i], data_stats, metrics, columns, subs, "SERIES")


def run_os_significance_repls_analysis():
    roots = [
        "{0}/Results/os-synth/aht/ens-sign/ens-sign-se".format(HOME),
        "{0}/Results/os-synth/aht/ens-sign/ens-sign-pew".format(HOME),
        "{0}/Results/os-synth/sgd/ens-sign/ens-sign-se".format(HOME),
        "{0}/Results/os-synth/sgd/ens-sign/ens-sign-pew".format(HOME)
    ]

    out_paths = [
        "aht_ens_sign_se_rep_synth.xls",
        "aht_ens_sign_pew_rep_synth.xls",
        "sgd_ens_sign_se_rep_synth.xls",
        "sgd_ens_sign_pew_rep_synth.xls",
    ]

    latex_tables_out_paths = list(map(lambda x: "latex_tables_" + x, out_paths))

    metrics = ["baseCorrectReplacements", "baseIncorrectReplacements", "osCorrectReplacements", "osIncorrectReplacements"]
    columns = ["1.0", "0.5", "0.2", "0.1", "0.05", "0.01"]
    subs = ["all"]

    for i in range(0, len(roots)):
        data = load.load_series_data([roots[i]], metrics, columns, True)
        data_stats = stats.compute_replacements_statistics(data)
        save.write_stats_to_file(out_paths[i], data_stats, metrics, columns, subs)
        save.write_latex_formulas_to_file(latex_tables_out_paths[i], data_stats, metrics, columns, subs, "TABLE")

        ticks_series_data = stats.generate_replacement_ticks(data)
        save.write_series_to_file("ticks", ticks_series_data)


def run_os_final_analysis():
    roots = [
        [
            "{0}/Results/os/os-real/aht/final-risky".format(HOME),
            "{0}/Results/os/os-real/aht/final-less".format(HOME),
            "{0}/Results/os/os-real/aht/final-other".format(HOME),
            "{0}/Results/os/os-real/aht/final-other2".format(HOME),
            "{0}/Results/os/os-real/aht/det".format(HOME)

         ], [
            "{0}/Results/os/os-real/sgd/final-risky".format(HOME),
            "{0}/Results/os/os-real/sgd/final-less".format(HOME),
            "{0}/Results/os/os-real/sgd/final-other".format(HOME),
            "{0}/Results/os/os-real/sgd/final-other2".format(HOME),
            "{0}/Results/os/os-real/sgd/det".format(HOME)
        ]
    ]

    out_paths = [
        "aht_final.xls",
        "sgd_final.xls"
    ]

    roots_post = ["-risky", "-less", "", "", ""]

    metrics = ["accuracy", "kappa"]
    columns = ["1.0", "0.5", "0.2", "0.1", "0.05", "0.01"]
    subs = ["all"]

    for i in range(0, len(roots)):
        data = load.load_avg_data(roots[i], metrics, columns, roots_post, True)
        data_stats = stats.compute_avg_statistics(data)
        save.write_stats_to_file(out_paths[i], data_stats, metrics, columns + ["avg"], subs)


def run_final_ranks_analysis():
    roots = [
        [
            "{0}/Results/os/os-real/aht/final-risky".format(HOME),
            "{0}/Results/os/os-real/aht/final-less".format(HOME),
            "{0}/Results/os/os-real/aht/final-other".format(HOME),
            "{0}/Results/os/os-real/aht/final-other2".format(HOME),
            "{0}/Results/os/os-real/aht/det".format(HOME)
        ], [
            "{0}/Results/os/os-real/sgd/final-risky".format(HOME),
            "{0}/Results/os/os-real/sgd/final-less".format(HOME),
            "{0}/Results/os/os-real/sgd/final-other".format(HOME),
            "{0}/Results/os/os-real/sgd/final-other2".format(HOME),
            "{0}/Results/os/os-real/sgd/det".format(HOME)
        ]
    ]

    roots_post = ["-risky", "-less", "", "", ""]

    out_paths = [
        "aht_final_ranks.xls",
        "sgd_final_ranks.xls"
    ]

    metrics = ["accuracy", "kappa"]
    columns = ["1.0", "0.5", "0.2", "0.1", "0.05", "0.01"]
    subs = ["all"]

    for i in range(0, len(roots)):
        data = load.load_avg_data(roots[i], metrics, columns, roots_post, True)
        data_stats = stats.compute_ranks_statistics(data)
        save.write_ranks_to_file(out_paths[i], data_stats, metrics, columns, subs)


def run_averages_extraction():
    roots = [
        [
            "{0}/Results/os/os-real/aht/final-risky".format(HOME),
            "{0}/Results/os/os-real/aht/final-less".format(HOME),
            "{0}/Results/os/os-real/aht/final-other".format(HOME),
            "{0}/Results/os/os-real/aht/final-other2".format(HOME),
        ], [
            "{0}/Results/os/os-real/sgd/final-risky".format(HOME),
            "{0}/Results/os/os-real/sgd/final-less".format(HOME),
            "{0}/Results/os/os-real/sgd/final-other".format(HOME),
            "{0}/Results/os/os-real/sgd/final-other2".format(HOME)
        ]
    ]

    algs = ['aht', 'sgd']
    metrics = ["kappa"]
    columns = ["1.0", "0.5", "0.2", "0.1", "0.05", "0.01"]
    roots_post = ["-risky", "-less", "", ""]
    col_groups = {'high': ['1.0', '0.5', '0.2'], 'low': ['0.1', '0.05', '0.01']}

    for i in range(0, len(roots)):
        data = load.load_avg_data(roots[i], metrics, columns, roots_post, True)
        merged_data = stats.merge_columns(data, col_groups)
        save.write_merged_data_to_file('./{0}-merged'.format(algs[i]), merged_data, col_groups)


def main():
    print("Running...")

    run_os_analysis()

    print("Done.")


if __name__ == "__main__":
    main()
