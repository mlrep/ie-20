from plots import series_plots as pgfplot
from plots import strat_plots as strat_plot

def generate_div_ens_figures():
    labels_map = {
        'ENS#CLUST-STAT': 'SCL',
        'ENS#CLUST': 'CL',
        'ENS#CLUST+SE': 'CL+FX',
        'ENS#CLUST+SERR': 'CL+ER',
        'ENS#CLUST-DIV': 'CLD',
        'ENS#CLUST-STAB': 'CLS',
        'ENS#CLUST-DIV+SE': 'CLD+FX',
        'ENS#CLUST-DIV+SERR': 'CLD+ER',
        'ENS#CLUST-STAB+SE': 'CLS+FX',
        'ENS#CLUST-STAB+SERR': 'CLS+ER',
        'ENS#BAG': 'BAG',
        'ENS#BAG-DIV': 'BAG-D',
        'ENS#BAG-STAB': 'BAG-S',
        'ENS#CLUST-L1': 'CL-1',
        'ENS#CLUST-L2': 'CL-2',
        'ENS#CLUST-L3': 'CL-3',
        'ENS#CLUST-L4': 'CL-4'
    }

    generate_ctrl(labels_map)
    generate_enh(labels_map)
    generate_cmb(labels_map)
    generate_rng(labels_map)
    generate_lbs(labels_map)


def generate_ctrl(labels_map):
    pgfplot.create_plot(
        input_path='D:\\Computer_Science\\PhD\\Papers\\2018_BigDataIEEE\\data\\synth\\ctrl\\ctrl_SEA1_acc.dat',
        size=600000, g=100, ag=[35, 35, 35], init=60000, p=[150000, 300000, 450000], w=100, omit=5000, lb_path=None,
        labels_map=labels_map, output_name='ctrl/ctrl_SEA1_acc', sel_series=['CLD', 'CLS', 'BAG-S'],
        legend=True, xticks=False, ylabel='Accuracy', title='SEA1')
    pgfplot.create_plot(
        input_path='D:\\Computer_Science\\PhD\\Papers\\2018_BigDataIEEE\\data\\synth\\ctrl\\ctrl_SEA1_D.dat',
        size=600000, g=100, ag=[30, 30, 30], init=60000, p=[150000, 300000, 450000], w=100, omit=5000, lb_path=None,
        labels_map=labels_map, output_name='ctrl/ctrl_SEA1_D', sel_series=['CLD', 'CLS', 'BAG-S'],
        legend=False, xticks=True, ylabel='D', title=None)

    pgfplot.create_plot(
        input_path='D:\\Computer_Science\\PhD\\Papers\\2018_BigDataIEEE\\data\\synth\\ctrl\\ctrl_STAGGER2_kappa.dat',
        size=600000, g=100, ag=[10, 10, 10], init=60000, p=[150000, 300000, 450000], w=10000, omit=1000, lb_path=None,
        labels_map=labels_map, output_name='ctrl/ctrl_STAGGER2_kappa', sel_series=['CLD', 'CLS', 'BAG-D'],
        legend=True, xticks=False, ylabel='Kappa', title='STAGGER2')
    pgfplot.create_plot(
        input_path='D:\\Computer_Science\\PhD\\Papers\\2018_BigDataIEEE\\data\\synth\\ctrl\\ctrl_STAGGER2_D.dat',
        size=600000, g=100, ag=[10, 10, 10], init=60000, p=[150000, 300000, 450000], w=10000, omit=1000, lb_path=None,
        labels_map=labels_map, output_name='ctrl/ctrl_STAGGER2_D', sel_series=['CLD', 'CLS', 'BAG-D'],
        legend=False, xticks=True, ylabel='D', title=None, yticks=[0.0, 0.15, 0.30, 0.45], ylim=[-0.05, 0.50])

    pgfplot.create_plot(
        input_path='D:\\Computer_Science\\PhD\\Papers\\2018_BigDataIEEE\\data\\synth\\ctrl\\ctrl_TREE3_acc.dat',
        size=1200000, g=100, ag=[20, 20, 20], init=120000, p=[400000, 800000], w=50000, omit=1000, lb_path=None,
        labels_map=labels_map, output_name='ctrl/ctrl_TREE3_acc', sel_series=['CLD', 'CLS', 'BAG-D'],
        legend=True, xticks=False, ylabel='Accuracy', title='TREE3', yticks=[0.20, 0.40, 0.60, 0.80, 1.00], ylim=[0.15, 1.05])
    pgfplot.create_plot(
        input_path='D:\\Computer_Science\\PhD\\Papers\\2018_BigDataIEEE\\data\\synth\\ctrl\\ctrl_TREE3_D.dat',
        size=1200000, g=100, ag=[20, 20, 20], init=120000, p=[400000, 800000], w=50000, omit=1000, lb_path=None,
        labels_map=labels_map, output_name='ctrl/ctrl_TREE3_D', sel_series=['CLD', 'CLS', 'BAG-D'],
        legend=False, xticks=True, ylabel='D', title=None)

    pgfplot.create_plot(
        input_path='D:\\Computer_Science\\PhD\\Papers\\2018_BigDataIEEE\\data\\synth\\ctrl\\ctrl_RBF4_kappa.dat',
        size=1200000, g=100, ag=[35, 35, 35], init=120000, p=[400000, 800000], w=100000, omit=60000, lb_path=None,
        labels_map=labels_map, output_name='ctrl/ctrl_RBF4_kappa', sel_series=['CLD', 'CLS', 'BAG-S'],
        legend=True, xticks=False, ylabel='Kappa', title='RBF4', yticks=[0.80, 0.85, 0.90, 0.95, 1.00], ylim=[0.79, 1.02])
    pgfplot.create_plot(
        input_path='D:\\Computer_Science\\PhD\\Papers\\2018_BigDataIEEE\\data\\synth\\ctrl\\ctrl_RBF4_D.dat',
        size=1200000, g=100, ag=[30, 30, 30], init=120000, p=[400000, 800000], w=100000, omit=60000, lb_path=None,
        labels_map=labels_map, output_name='ctrl/ctrl_RBF4_D', sel_series=['CLD', 'CLS', 'BAG-S'],
        legend=False, xticks=True, ylabel='D', title=None, yticks=[0.0, 0.10, 0.20, 0.30, 0.40], ylim=[-0.02, 0.42])


def generate_enh(labels_map):
    pgfplot.create_plot(
        input_path='D:\\Computer_Science\\PhD\\Papers\\2018_BigDataIEEE\\data\\synth\\enh\\enh_TREE1_acc.dat',
        size=1000000, g=100, ag=[25, 35, 25], init=100000, p=[250000, 500000, 750000], w=100, omit=1000, lb_path=None,
        labels_map=labels_map, output_name='enh/enh_TREE1_acc', sel_series=['CL', 'CL+FX', 'CL+ER'],
        legend=True, xticks=False, ylabel='Accuracy', title='TREE1', ylim=[0.10, 1.05])
    pgfplot.create_plot(
        input_path='D:\\Computer_Science\\PhD\\Papers\\2018_BigDataIEEE\\data\\synth\\enh\\enh_TREE1_D.dat',
        size=1000000, g=100, ag=[10, 35, 10], init=100000, p=[250000, 500000, 750000], w=100, omit=1000, lb_path=None,
        labels_map=labels_map, output_name='enh/enh_TREE1_D', sel_series=['CL', 'CL+FX', 'CL+ER'],
        legend=False, xticks=True, ylabel='D', title=None, ylim=[-0.02, 0.42])

    pgfplot.create_plot(
        input_path='D:\\Computer_Science\\PhD\\Papers\\2018_BigDataIEEE\\data\\synth\\enh\\enh_RBF2_kappa.dat',
        size=1000000, g=100, ag=[25, 35, 25], init=100000, p=[250000, 500000, 750000], w=10000, omit=1000, lb_path=None,
        labels_map=labels_map, output_name='enh/enh_RBF2_kappa', sel_series=['CL', 'CL+FX', 'CL+ER'],
        legend=True, xticks=False, ylabel='Kappa', title='RBF2')
    pgfplot.create_plot(
        input_path='D:\\Computer_Science\\PhD\\Papers\\2018_BigDataIEEE\\data\\synth\\enh\\enh_RBF2_D.dat',
        size=1000000, g=100, ag=[15, 20, 15], init=100000, p=[250000, 500000, 750000], w=10000, omit=1000, lb_path=None,
        labels_map=labels_map, output_name='enh/enh_RBF2_D', sel_series=['CL', 'CL+FX', 'CL+ER'],
        legend=False, xticks=True, ylabel='D', title=None, ylim=[-0.02, 0.27])

    pgfplot.create_plot(
        input_path='D:\\Computer_Science\\PhD\\Papers\\2018_BigDataIEEE\\data\\synth\\enh\\enh_HYPERPLANE1_acc.dat',
        size=500000, g=100, ag=[20, 20, 20], init=50000, p=[], w=0, omit=1000, lb_path=None,
        labels_map=labels_map, output_name='enh/enh_HYPERPLANE1_acc', sel_series=['CL', 'CL+FX', 'CL+ER'],
        legend=True, xticks=False, ylabel='Accuracy', title='HYPER1', yticks=[0.65, 0.75, 0.85, 0.95], ylim=[0.62, 0.97])
    pgfplot.create_plot(
        input_path='D:\\Computer_Science\\PhD\\Papers\\2018_BigDataIEEE\\data\\synth\\enh\\enh_HYPERPLANE1_D.dat',
        size=500000, g=100, ag=[20, 20, 20], init=50000, p=[], w=0, omit=1000, lb_path=None,
        labels_map=labels_map, output_name='enh/enh_HYPERPLANE1_D', sel_series=['CL', 'CL+FX', 'CL+ER'],
        legend=False, xticks=True, ylabel='D', title=None, yticks=[0.0, 0.10, 0.20, 0.30, 0.40], ylim=[-0.02, 0.44])

    pgfplot.create_plot(
        input_path='D:\\Computer_Science\\PhD\\Papers\\2018_BigDataIEEE\\data\\synth\\enh\\enh_TREE4_kappa.dat',
        size=1200000, g=100, ag=[25, 35, 25], init=120000, p=[400000, 800000], w=100000, omit=1000, lb_path=None,
        labels_map=labels_map, output_name='enh/enh_TREE4_kappa', sel_series=['CL', 'CL+FX', 'CL+ER'],
        legend=True, xticks=False, ylabel='Kappa', title='TREE4', yticks=[0.10, 0.40, 0.70, 1.00], ylim=[0.08, 1.05])
    pgfplot.create_plot(
        input_path='D:\\Computer_Science\\PhD\\Papers\\2018_BigDataIEEE\\data\\synth\\enh\\enh_TREE4_DF.dat',
        size=1200000, g=100, ag=[25, 40, 25], init=120000, p=[400000, 800000], w=100000, omit=1000, lb_path=None,
        labels_map=labels_map, output_name='enh/enh_TREE4_DF', sel_series=['CL', 'CL+FX', 'CL+ER'],
        legend=False, xticks=True, ylabel='DF', title=None, yticks=[0.10, 0.30, 0.50, 0.70])


def generate_cmb(labels_map):
    pgfplot.create_plot(
        input_path='D:\\Computer_Science\\PhD\\Papers\\2018_BigDataIEEE\\data\\synth\\cmb\\cmb_SEA2_acc.dat',
        size=600000, g=100, ag=[35, 35, 35], init=60000, p=[150000, 300000, 450000], w=10000, omit=5000, lb_path=None,
        labels_map=labels_map, output_name='cmb/cmb_SEA2_acc', sel_series=['CLD+FX', 'CLS+FX', 'CLD+ER'],
        legend=True, xticks=False, ylabel='Accuracy', title='SEA2', yticks=[0.60, 0.70, 0.80, 0.90, 1.00], ylim=[0.58, 1.02])
    pgfplot.create_plot(
        input_path='D:\\Computer_Science\\PhD\\Papers\\2018_BigDataIEEE\\data\\synth\\cmb\\cmb_SEA2_DF.dat',
        size=600000, g=100, ag=[25, 25, 25], init=60000, p=[150000, 300000, 450000], w=10000, omit=5000, lb_path=None,
        labels_map=labels_map, output_name='cmb/cmb_SEA2_DF', sel_series=['CLD+FX', 'CLS+FX', 'CLD+ER'],
        legend=False, xticks=True, ylabel='DF', title=None, yticks=[0.00, 0.10, 0.20, 0.30, 0.40], ylim=[-0.02, 0.42])

    pgfplot.create_plot(
        input_path='D:\\Computer_Science\\PhD\\Papers\\2018_BigDataIEEE\\data\\synth\\cmb\\cmb_TREE2_kappa.dat',
        size=1000000, g=100, ag=[30, 30, 30], init=100000, p=[250000, 500000, 750000], w=10000, omit=1000, lb_path=None,
        labels_map=labels_map, output_name='cmb/cmb_TREE2_kappa', sel_series=['CLD+ER', 'CLS+ER', 'BAG-S'],
        legend=True, xticks=False, ylabel='Kappa', title='TREE2', yticks=[0.00, 0.25, 0.50, 0.75, 1.00], ylim=[-0.02, 1.02])
    pgfplot.create_plot(
        input_path='D:\\Computer_Science\\PhD\\Papers\\2018_BigDataIEEE\\data\\synth\\cmb\\cmb_TREE2_D.dat',
        size=1000000, g=100, ag=[20, 15, 20], init=100000, p=[250000, 500000, 750000], w=10000, omit=1000, lb_path=None,
        labels_map=labels_map, output_name='cmb/cmb_TREE2_D', sel_series=['CLD+ER', 'CLS+ER', 'BAG-S'],
        legend=False, xticks=True, ylabel='D', title=None, yticks=[0.00, 0.10, 0.20, 0.30, 0.40], ylim=[-0.02, 0.42])

    # pgfplot.create_plot(
    #     input_path='D:\\Computer_Science\\PhD\\Papers\\2018_BigDataIEEE\\data\\synth\\cmb\\cmb_RBF3_acc.dat',
    #     size=1200000, g=100, ag=[35, 35, 35], init=120000, p=[400000, 800000], w=50000, omit=80000, lb_path=None,
    #     labels_map=labels_map, output_name='cmb/cmb_RBF3_acc', sel_series=['CLD+SE', 'CLD+SR', 'BAG-L'],
    #     legend=True, xticks=False, ylabel='Accuracy', title='RBF3')
    # pgfplot.create_plot(
    #     input_path='D:\\Computer_Science\\PhD\\Papers\\2018_BigDataIEEE\\data\\synth\\cmb\\cmb_RBF3_D.dat',
    #     size=1200000, g=100, ag=[35, 35, 35], init=120000, p=[400000, 800000], w=50000, omit=80000, lb_path=None,
    #     labels_map=labels_map, output_name='cmb/cmb_RBF3_D', sel_series=['CLD+SE', 'CLD+SR', 'BAG-L'],
    #     legend=False, xticks=True, ylabel='D', title=None)
    #
    # pgfplot.create_plot(
    #     input_path='D:\\Computer_Science\\PhD\\Papers\\2018_BigDataIEEE\\data\\synth\\cmb\\cmb_HYPERPLANE2_acc.dat',
    #     size=500000, g=100, ag=[25, 25, 20, 20], init=50000, p=[], w=0, omit=1000, lb_path=None,
    #     labels_map=labels_map, output_name='cmb/cmb_HYPERPLANE2_acc', sel_series=['CLD+SE', 'CLS+SE', 'CLD+SR', 'CLS+SR'],
    #     legend=True, xticks=False, ylabel=None, title='HYPER2')
    # pgfplot.create_plot(
    #     input_path='D:\\Computer_Science\\PhD\\Papers\\2018_BigDataIEEE\\data\\synth\\cmb\\cmb_HYPERPLANE2_D.dat',
    #     size=500000, g=100, ag=[25, 25, 20, 20], init=50000, p=[], w=0, omit=1000, lb_path=None,
    #     labels_map=labels_map, output_name='cmb/cmb_HYPERPLANE2_D', sel_series=['CLD+SE', 'CLS+SE', 'CLD+SR', 'CLS+SR'],
    #     legend=False, xticks=True, ylabel=None, title=None)


def generate_rng(labels_map):
    pgfplot.create_plot(
        input_path='D:\\Computer_Science\\PhD\\Papers\\2018_BigDataIEEE\\data\\synth\\rng\\rng_RBF2_acc.dat',
        size=1000000, g=100, ag=[30, 20], init=100000, p=[250000, 500000, 750000], w=10000, omit=5000, lb_path=None,
        labels_map=labels_map, output_name='rng/rng_RBF2_acc', sel_series=['SCL', 'CL'],
        legend=True, xticks=False, ylabel='Accuracy', title='RBF2')
    pgfplot.create_plot(
        input_path='D:\\Computer_Science\\PhD\\Papers\\2018_BigDataIEEE\\data\\synth\\rng\\rng_RBF2_DF.dat',
        size=1000000, g=100, ag=[20, 20], init=100000, p=[250000, 500000, 750000], w=10000, omit=5000, lb_path=None,
        labels_map=labels_map, output_name='rng/rng_RBF2_DF', sel_series=['SCL', 'CL'],
        legend=False, xticks=True, ylabel='DF', title=None)

    pgfplot.create_plot(
        input_path='D:\\Computer_Science\\PhD\\Papers\\2018_BigDataIEEE\\data\\synth\\rng\\rng_TREE4_kappa.dat',
        size=1200000, g=100, ag=[25, 15], init=120000, p=[400000, 800000], w=100000, omit=1000, lb_path=None,
        labels_map=labels_map, output_name='rng/rng_TREE4_kappa', sel_series=['SCL', 'CL'],
        legend=True, xticks=False, ylabel='Kappa', title='TREE4', yticks=[0.10, 0.30, 0.50, 0.70, 0.90])
    pgfplot.create_plot(
        input_path='D:\\Computer_Science\\PhD\\Papers\\2018_BigDataIEEE\\data\\synth\\rng\\rng_TREE4_D.dat',
        size=1200000, g=100, ag=[15, 15], init=120000, p=[400000, 800000], w=100000, omit=1000, lb_path=None,
        labels_map=labels_map, output_name='rng/rng_TREE4_D', sel_series=['SCL', 'CL'],
        legend=False, xticks=True, ylabel='D', title=None)


def generate_lbs(labels_map):
    pgfplot.create_plot(
        input_path='D:\\Computer_Science\\PhD\\Papers\\2018_BigDataIEEE\\data\\synth\\lbs\\lbs_TREE_CLL.dat',
        size=1000000, g=100, ag=[1, 1, 1, 1], init=100000, p=[], w=0, omit=0, lb_path=None,
        labels_map=labels_map, output_name='lbs/lbs_TREE_acc', sel_series=['CL-1', 'CL-2', 'CL-3', 'CL-4'],
        legend=True, xticks=True, ylabel='Accuracy', title='TREE')

    pgfplot.create_plot(
        input_path='D:\\Computer_Science\\PhD\\Papers\\2018_BigDataIEEE\\data\\synth\\lbs\\lbs_RBF_CLL.dat',
        size=1000000, g=100, ag=[1, 1, 1, 1], init=100000, p=[], w=0, omit=0, lb_path=None,
        labels_map=labels_map, output_name='lbs/lbs_RBF_kappa', sel_series=['CL-1', 'CL-2', 'CL-3', 'CL-4'],
        legend=True, xticks=True, ylabel='Kappa', title='RBF')


def generate_inst_expl_figures():
    # labels_map = {
    #     'ERR-BASE': 'BASE',
    #     'ERR-100': '100',
    #     'ERR-10k': '10k'
    # }
    # generate_err(labels_map)

    labels_map = {
        'OS#PEW+ENS+switch-0.1': 'PEWS',
        'OS#PEW+ENS+elev-0.1': 'PEWE',
        'OS#SE+ENS+switch-0.1': 'SES',
        'OS#SE-0.1': 'SE',
        'OS#ALS-0.1': 'ALS',
        'OS#ALR-0.1': 'ALR',
        'OS#ALRV-0.1': 'ALRV',
        'OS#ADOB-0.1': 'ADOB',
        'OS#BAG-0.1': 'OBAG',
        'OS#DWM-0.1': 'DWM',
        'OS#AUC-0.1': 'AUC',
        'OS#AWE-0.1': 'AWE'
    }
    generate_series(labels_map)


def generate_err(labels_map):
    pgfplot.create_plot(
        input_path='/home/lk/Papers/2019_PR_RiskyAdaptation/data/err/aht-tree3/err_aht_50.data',
        size=1200000, g=100, ag=[100, 35, 35], p=None, w=None, omit=0, lb_path=None,
        labels_map=labels_map, output_name='err_TREE3_50', sel_series=['BASE', '10k', '100'],
        legend=True, ylabel='Error', title='AHT / TREE3 / 50%', ylim=(-0.05, 0.62), yticks=[0.0, 0.15, 0.3, 0.45, 0.6])
    pgfplot.create_plot(
        input_path='/home/lk/Papers/2019_PR_RiskyAdaptation/data/err/aht-tree3/err_aht_10.data',
        size=1200000, g=100, ag=[100, 35, 35], p=None, w=None, omit=0, lb_path=None,
        labels_map=labels_map, output_name='err_TREE3_10', sel_series=['BASE', '10k', '100'],
        legend=True, ylabel=None, title='AHT / TREE3 / 10%', ylim=(-0.05, 0.65), yticks=[0.0, 0.2, 0.4, 0.6])
    pgfplot.create_plot(
        input_path='/home/lk/Papers/2019_PR_RiskyAdaptation/data/err/aht-tree3/err_aht_1.data',
        size=1200000, g=100, ag=[100, 35, 35], p=None, w=None, omit=0, lb_path=None,
        labels_map=labels_map, output_name='err_TREE3_1', sel_series=['BASE', '10k', '100'],
        legend=True, ylabel=None, title='AHT / TREE3 / 1%', ylim=(-0.05, 0.85), yticks=[0.0, 0.2, 0.4, 0.6, 0.8])

    pgfplot.create_plot(
        input_path='/home/lk/Papers/2019_PR_RiskyAdaptation/data/err/sgd-stagger2/err_sgd_50.data',
        size=600000, g=100, ag=[100, 35, 35], p=None, w=None, omit=0, lb_path=None,
        labels_map=labels_map, output_name='err_STAGGER2_50', sel_series=['BASE', '10k', '100'],
        legend=True, ylabel='Error', title='SGD / STAG2 / 50%')
    pgfplot.create_plot(
        input_path='/home/lk/Papers/2019_PR_RiskyAdaptation/data/err/sgd-stagger2/err_sgd_10.data',
        size=600000, g=100, ag=[100, 35, 35], p=None, w=None, omit=0, lb_path=None,
        labels_map=labels_map, output_name='err_STAGGER2_10', sel_series=['BASE', '10k', '100'],
        legend=True, ylabel=None, title='SGD / STAG2 / 10%', ylim=(-0.05, 0.89), yticks=[0.0, 0.25, 0.5, 0.75])
    pgfplot.create_plot(
        input_path='/home/lk/Papers/2019_PR_RiskyAdaptation/data/err/sgd-stagger2/err_sgd_1.data',
        size=600000, g=100, ag=[100, 35, 35], p=None, w=None, omit=0, lb_path=None,
        labels_map=labels_map, output_name='err_STAGGER2_1', sel_series=['BASE', '10k', '100'],
        legend=True, ylabel=None, title='SGD / STAG2 / 1%', ylim=(-0.05, 0.8), yticks=[0.0, 0.25, 0.5, 0.75])


def generate_series(labels_map):
    # AHT
    pgfplot.create_plot(
        input_path='/home/lk/Papers/2019_PR_RiskyAdaptation/data/aht-series/activity_accuracy_series.data',
        size=10853, g=100, ag=[1, 1, 1], p=None, w=None, omit=0, lb_path=None,
        labels_map=labels_map, output_name='aht_activity', sel_series=['ALS', 'ADOB', 'PEWS'],
        legend=True, ylabel=None, title='ACTIVITY', xticks=[2500, 5000, 7500, 10000], ylim=(-0.05, 1.05), yticks=[0.0, 0.25, 0.5, 0.75, 1.0])
    pgfplot.create_plot(
        input_path='/home/lk/Papers/2019_PR_RiskyAdaptation/data/aht-series/activity_raw_accuracy_series.data',
        size=1048570, g=2500, ag=[3, 3, 3], p=None, w=None, omit=0, lb_path=None,
        labels_map=labels_map, output_name='aht_activity_raw', sel_series=['ALR', 'DWM', 'SES'],
        legend=True, ylabel=None, title='ACTIVITY_RAW', xticks=[250000, 500000, 750000, 1000000], ylim=(-0.05, 1.05), yticks=[0.0, 0.25, 0.5, 0.75, 1.0])
    pgfplot.create_plot(
        input_path='/home/lk/Papers/2019_PR_RiskyAdaptation/data/aht-series/airlines_accuracy_series.data',
        size=539384, g=2500, ag=[3, 3, 3], p=None, w=None, omit=0, lb_path=None,
        labels_map=labels_map, output_name='aht_airlines', sel_series=['ALS', 'DWM', 'SES'],
        legend=True, ylabel=None, title='AIRLINES', xticks=[50000, 200000, 350000, 500000], ylim=(0.45, 0.85), yticks=[0.5, 0.6, 0.7, 0.8])
    pgfplot.create_plot(
        input_path='/home/lk/Papers/2019_PR_RiskyAdaptation/data/aht-series/connect4_accuracy_series.data',
        size=67557, g=500, ag=[2, 2, 2], p=None, w=None, omit=0, lb_path=None,
        labels_map=labels_map, output_name='aht_connect4', sel_series=['ALR', 'DWM', 'SES'],
        legend=True, ylabel=None, title='CONNECT4', xticks=[2500, 5000, 7500, 10000], ylim=(-0.05, 1.05), yticks=[0.0, 0.25, 0.5, 0.75, 1.0])
    pgfplot.create_plot(
        input_path='/home/lk/Papers/2019_PR_RiskyAdaptation/data/aht-series/covertype_accuracy_series.data',
        size=581012, g=2500, ag=[3, 3, 3], p=None, w=None, omit=0, lb_path=None,
        labels_map=labels_map, output_name='aht_covertype', sel_series=['ALRV', 'DWM', 'PEWS'],
        legend=True, ylabel=None, title='COVERTYPE', xticks=[50000, 200000, 350000, 500000], ylim=(0.35, 1.05), yticks=[0.4, 0.6, 0.8, 1.0])
    pgfplot.create_plot(
        input_path='/home/lk/Papers/2019_PR_RiskyAdaptation/data/aht-series/crimes_accuracy_series.data',
        size=878049, g=3000, ag=[3, 3, 3], p=None, w=None, omit=0, lb_path=None,
        labels_map=labels_map, output_name='aht_crimes', sel_series=['ALR', 'DWM', 'PEWS'],
        legend=True, ylabel=None, title='CRIMES', xticks=[250000, 500000, 750000], ylim=(0.14, 0.31), yticks=[0.15, 0.2, 0.25, 0.3])
    pgfplot.create_plot(
        input_path='/home/lk/Papers/2019_PR_RiskyAdaptation/data/aht-series/dj30_accuracy_series.data',
        size=138166, g=500, ag=[3, 3, 3], p=None, w=None, omit=0, lb_path=None,
        labels_map=labels_map, output_name='aht_dj30', sel_series=['ALRV', 'AUC', 'PEWS'],
        legend=True, ylabel=None, title='DJ30', xticks=[30000, 60000, 90000, 120000])
    pgfplot.create_plot(
        input_path='/home/lk/Papers/2019_PR_RiskyAdaptation/data/aht-series/eeg_accuracy_series.data',
        size=14980, g=100, ag=[3, 3, 3], p=None, w=None, omit=0, lb_path=None,
        labels_map=labels_map, output_name='aht_eeg', sel_series=['ALRV', 'OBAG', 'PEWS'],
        legend=True, ylabel=None, title='EEG')
    pgfplot.create_plot(
        input_path='/home/lk/Papers/2019_PR_RiskyAdaptation/data/aht-series/elec_accuracy_series.data',
        size=45312, g=100, ag=[5, 5, 5], p=None, w=None, omit=0, lb_path=None,
        labels_map=labels_map, output_name='aht_elec', sel_series=['ALRV', 'OBAG', 'SES'],
        legend=True, ylabel=None, title='ELEC', xticks=[10000, 20000, 30000, 40000], ylim=(0.55, 0.95), yticks=[0.6, 0.7, 0.8, 0.9])
    pgfplot.create_plot(
        input_path='/home/lk/Papers/2019_PR_RiskyAdaptation/data/aht-series/gas_accuracy_series.data',
        size=13910, g=100, ag=[2, 2, 2], p=None, w=None, omit=0, lb_path=None,
        labels_map=labels_map, output_name='aht_gas', sel_series=['ALS', 'DWM', 'PEWS'],
        legend=True, ylabel=None, title='GAS', xticks=[3000, 6000, 9000, 12000], ylim=(0.15, 1.05), yticks=[0.2, 0.4, 0.6, 0.8, 1.0])
    pgfplot.create_plot(
        input_path='/home/lk/Papers/2019_PR_RiskyAdaptation/data/aht-series/poker_accuracy_series.data',
        size=829201, g=3000, ag=[3, 3, 3], p=None, w=None, omit=0, lb_path=None,
        labels_map=labels_map, output_name='aht_poker', sel_series=['ALR', 'DWM', 'PEWS'],
        legend=True, ylabel=None, title='POKER', xticks=[250000, 500000, 750000])
    pgfplot.create_plot(
        input_path='/home/lk/Papers/2019_PR_RiskyAdaptation/data/aht-series/sensor_accuracy_series.data',
        size=2219802, g=10000, ag=[3, 3, 3], p=None, w=None, omit=0, lb_path=None,
        labels_map=labels_map, output_name='aht_sensor', sel_series=['ALR', 'DWM', 'PEWS'],
        legend=True, ylabel=None, title='SENSOR', xticks=[500000, 1000000, 1500000, 2000000])
    pgfplot.create_plot(
        input_path='/home/lk/Papers/2019_PR_RiskyAdaptation/data/aht-series/spam_accuracy_series.data',
        size=9324, g=100, ag=[1, 1, 1], p=None, w=None, omit=0, lb_path=None,
        labels_map=labels_map, output_name='aht_spam', sel_series=['ALS', 'ADOB', 'PEWS'],
        legend=True, ylabel=None, title='SPAM', xticks=[2500, 5000, 7500], ylim=(0.15, 1.05), yticks=[0.2, 0.4, 0.6, 0.8, 1.0])
    pgfplot.create_plot(
        input_path='/home/lk/Papers/2019_PR_RiskyAdaptation/data/aht-series/weather_accuracy_series.data',
        size=18158, g=200, ag=[1, 1, 1], p=None, w=None, omit=0, lb_path=None,
        labels_map=labels_map, output_name='aht_weather', sel_series=['ALS', 'OBAG', 'PEWS'],
        legend=True, ylabel=None, title='WEATHER', xticks=[5000, 10000, 15000], ylim=(0.59, 0.76), yticks=[0.6, 0.65, 0.7, 0.75])

    # SGD
    pgfplot.create_plot(
        input_path='/home/lk/Papers/2019_PR_RiskyAdaptation/data/sgd-series/activity_accuracy_series.data',
        size=10853, g=100, ag=[1, 1, 1], p=None, w=None, omit=0, lb_path=None,
        labels_map=labels_map, output_name='sgd_activity', sel_series=['ALR', 'ADOB', 'SES'],
        legend=True, ylabel=None, title='ACTIVITY', xticks=[2500, 5000, 7500, 10000], ylim=(-0.05, 1.05), yticks=[0.0, 0.25, 0.5, 0.75, 1.0])
    pgfplot.create_plot(
        input_path='/home/lk/Papers/2019_PR_RiskyAdaptation/data/sgd-series/activity_raw_accuracy_series.data',
        size=1048570, g=2500, ag=[3, 3, 3], p=None, w=None, omit=0, lb_path=None,
        labels_map=labels_map, output_name='sgd_activity_raw', sel_series=['ALR', 'ADOB', 'SE'],
        legend=True, ylabel=None, title='ACTIVITY_RAW', xticks=[250000, 500000, 750000, 1000000], ylim=(0.2, 1.05), yticks=[0.25, 0.5, 0.75, 1.0])
    pgfplot.create_plot(
        input_path='/home/lk/Papers/2019_PR_RiskyAdaptation/data/sgd-series/airlines_accuracy_series.data',
        size=539384, g=2500, ag=[3, 3, 3], p=None, w=None, omit=0, lb_path=None,
        labels_map=labels_map, output_name='sgd_airlines', sel_series=['ALRV', 'OBAG', 'SES'],
        legend=True, ylabel=None, title='AIRLINES', xticks=[50000, 200000, 350000, 500000])
    pgfplot.create_plot(
        input_path='/home/lk/Papers/2019_PR_RiskyAdaptation/data/sgd-series/connect4_accuracy_series.data',
        size=67557, g=500, ag=[2, 2, 2], p=None, w=None, omit=0, lb_path=None,
        labels_map=labels_map, output_name='sgd_connect4', sel_series=['ALR', 'ADOB', 'SE'],
        legend=True, ylabel=None, title='CONNECT4')
    pgfplot.create_plot(
        input_path='/home/lk/Papers/2019_PR_RiskyAdaptation/data/sgd-series/covertype_accuracy_series.data',
        size=581012, g=2500, ag=[3, 3, 3], p=None, w=None, omit=0, lb_path=None,
        labels_map=labels_map, output_name='sgd_covertype', sel_series=['ALR', 'ADOB', 'SE'],
        legend=True, ylabel=None, title='COVERTYPE', xticks=[50000, 200000, 350000, 500000], ylim=(-0.05, 1.05), yticks=[0.0, 0.25, 0.5, 0.75, 1.0])
    pgfplot.create_plot(
        input_path='/home/lk/Papers/2019_PR_RiskyAdaptation/data/sgd-series/crimes_accuracy_series.data',
        size=878049, g=3000, ag=[3, 3, 3], p=None, w=None, omit=0, lb_path=None,
        labels_map=labels_map, output_name='sgd_crimes', sel_series=['ALS', 'AWE', 'SE'],
        legend=True, ylabel=None, title='CRIMES', xticks=[250000, 500000, 750000], ylim=(0.03, 0.27), yticks=[0.05, 0.1, 0.15, 0.2, 0.25])
    pgfplot.create_plot(
        input_path='/home/lk/Papers/2019_PR_RiskyAdaptation/data/sgd-series/dj30_accuracy_series.data',
        size=138166, g=500, ag=[3, 3, 3], p=None, w=None, omit=5000, lb_path=None,
        labels_map=labels_map, output_name='sgd_dj30', sel_series=['ALS', 'OBAG', 'SE'],
        legend=True, ylabel=None, title='DJ30', xticks=[30000, 60000, 90000, 120000])
    pgfplot.create_plot(
        input_path='/home/lk/Papers/2019_PR_RiskyAdaptation/data/sgd-series/eeg_accuracy_series.data',
        size=14980, g=100, ag=[3, 3, 3], p=None, w=None, omit=0, lb_path=None,
        labels_map=labels_map, output_name='sgd_eeg', sel_series=['ALRV', 'ADOB', 'SE'],
        legend=True, ylabel=None, title='EEG')
    pgfplot.create_plot(
        input_path='/home/lk/Papers/2019_PR_RiskyAdaptation/data/sgd-series/elec_accuracy_series.data',
        size=45312, g=100, ag=[5, 5, 5], p=None, w=None, omit=0, lb_path=None,
        labels_map=labels_map, output_name='sgd_elec', sel_series=['ALR', 'ADOB', 'SES'],
        legend=True, ylabel=None, title='ELEC', xticks=[10000, 20000, 30000, 40000])
    pgfplot.create_plot(
        input_path='/home/lk/Papers/2019_PR_RiskyAdaptation/data/sgd-series/gas_accuracy_series.data',
        size=13910, g=100, ag=[2, 2, 2], p=None, w=None, omit=0, lb_path=None,
        labels_map=labels_map, output_name='sgd_gas', sel_series=['ALS', 'OBAG', 'SES'],
        legend=True, ylabel=None, title='GAS', xticks=[3000, 6000, 9000, 12000], ylim=(0.3, 1.05), yticks=[0.4, 0.6, 0.8, 1.0])
    pgfplot.create_plot(
        input_path='/home/lk/Papers/2019_PR_RiskyAdaptation/data/sgd-series/poker_accuracy_series.data',
        size=829201, g=3000, ag=[6, 6, 6], p=None, w=None, omit=0, lb_path=None,
        labels_map=labels_map, output_name='sgd_poker', sel_series=['ALRV', 'ADOB', 'SE'],
        legend=True, ylabel=None, title='POKER', xticks=[250000, 500000, 750000], ylim=(0.15, 0.85), yticks=[0.2, 0.4, 0.6, 0.8])
    pgfplot.create_plot(
        input_path='/home/lk/Papers/2019_PR_RiskyAdaptation/data/sgd-series/sensor_accuracy_series.data',
        size=2219802, g=10000, ag=[3, 3, 3], p=None, w=None, omit=10000, lb_path=None,
        labels_map=labels_map, output_name='sgd_sensor', sel_series=['ALS', 'AWE', 'SE'],
        legend=True, ylabel=None, title='SENSOR', xticks=[500000, 1000000, 1500000, 2000000])
    pgfplot.create_plot(
        input_path='/home/lk/Papers/2019_PR_RiskyAdaptation/data/sgd-series/spam_accuracy_series.data',
        size=9324, g=100, ag=[1, 1, 1], p=None, w=None, omit=0, lb_path=None,
        labels_map=labels_map, output_name='sgd_spam', sel_series=['ALRV', 'ADOB', 'SES'],
        legend=True, ylabel=None, title='SPAM', xticks=[2500, 5000, 7500], ylim=(-0.05, 1.05), yticks=[0.0, 0.2, 0.4, 0.6, 0.8, 1.0])
    pgfplot.create_plot(
        input_path='/home/lk/Papers/2019_PR_RiskyAdaptation/data/sgd-series/weather_accuracy_series.data',
        size=18158, g=200, ag=[1, 1, 1], p=None, w=None, omit=0, lb_path=None,
        labels_map=labels_map, output_name='sgd_weather', sel_series=['ALRV', 'OBAG', 'SE'],
        legend=True, ylabel=None, title='WEATHER', xticks=[5000, 10000, 15000], ylim=(0.53, 0.76), yticks=[0.55, 0.6, 0.65, 0.7, 0.75])


def generate_exploit_strat_vis():
    strat_plot.create_plot(1000, 200)


def main():
    print("Running...")
    #generate_inst_expl_figures()
    generate_exploit_strat_vis()


if __name__ == "__main__":
    main()
