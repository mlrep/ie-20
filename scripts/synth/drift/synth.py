import math
import random
import copy
import utils.arff as arff

root_dir = 'D:/Computer_Science/Projects/Data/streams'


def create_drifting_stream(streams, drift_defs, s_size):
    print('Creating drifting stream')
    drifting_stream = []
    d_idx = 0
    drift = drift_defs[d_idx]
    print(drift)

    for i in range(0, s_size):
        prob = sigm(drift['p'], drift['w'], i)
        r = random.uniform(0, 1)

        if prob > r:
            new_sample = streams[drift['c'][0]][i]
        else:
            new_sample = streams[drift['c'][1]][i]

        drifting_stream.append(new_sample)

        if (i > drift['p'] + drift['w'] / 2.0) and d_idx < len(drift_defs) - 1:
            d_idx += 1
            drift = drift_defs[d_idx]
            print(drift)

    return drifting_stream


def sigm(p, w, x):
    try:
        ans = 1 - (1.0 / (1 + math.exp((-4.0 / w) * (x - p))))
    except OverflowError:
        if x < p:
            ans = 1
        else:
            ans = 0
    return ans


def create_real_drifting_stream(base_stream, drift_defs, mappings, s_size):
    print('Creating drifting stream')
    drifting_stream = []
    d_idx = 0
    drift = drift_defs[d_idx]
    c_idx = 0
    concepts = drift_defs[c_idx]['c']
    dc_num = 0
    first_pw = drift_defs[0]['p'] - (drift_defs[0]['w'] / 2)

    print(drift)
    print(concepts)

    for i in range(0, s_size):
        prob = sigm(drift['p'], drift['w'], i)
        r = random.uniform(0, 1)
        base_row = copy.copy(base_stream[i])
        #print(i, base_row)

        if prob > r:
            new_sample_cls = mappings[drift['c'][0]][base_row[-1]]
        else:
            new_sample_cls = mappings[drift['c'][1]][base_row[-1]]

        if new_sample_cls != mappings[concepts[0]][base_row[-1]] and i >= first_pw:
            dc_num += 1

        base_row[-1] = new_sample_cls
        drifting_stream.append(base_row)

        if (i > drift['p'] + drift['w'] / 2.0) and d_idx < len(drift_defs) - 1:
            d_idx += 1
            drift = drift_defs[d_idx]
            print(drift)

        if d_idx >= 1 and i == drift['p'] - drift['w'] / 2.0:
            concepts = drift_defs[d_idx]['c']
            print(i, concepts)

    print(dc_num, s_size - first_pw)
    dc_ratio = dc_num / (s_size - first_pw)
    print(dc_ratio)

    return drifting_stream


def create_multiple_synth_drifting_streams(name, p1, p2, d, n):
    arffs = arff.load_arffs(['synth_base/{0}_r1_c1'.format(name), 'synth_base/{0}_r2_c1'.format(name), 'synth_base/{0}_r2_c2'.format(name),
                             'synth_base/{0}_r3_c1'.format(name), 'synth_base/{0}_r3_c2'.format(name)], root_dir)
    [streams, header] = arff.split_similar_arffs_fields(arffs)

    ds = create_drifting_stream(streams, [{'p': p1, 'w': 100, 'c': ['{0}_r1_c1'.format(name), '{0}_r2_c1'.format(name)]}], n)
    arff.write_arff(header, ds, 'imbalanced/dynamic/{0}_R_W100'.format(name), root_dir)
    ds = create_drifting_stream(streams, [{'p': p1, 'w': 100, 'c': ['{0}_r1_c1'.format(name), '{0}_r2_c2'.format(name)]}], n)
    arff.write_arff(header, ds, 'imbalanced/dynamic/{0}_RC_W100'.format(name), root_dir)
    ds = create_drifting_stream(streams, [{'p': p1, 'w': 100000, 'c': ['{0}_r1_c1'.format(name), '{0}_r2_c1'.format(name)]}], n)
    arff.write_arff(header, ds, 'imbalanced/dynamic/{0}_R_W100k'.format(name), root_dir)
    ds = create_drifting_stream(streams, [{'p': p1, 'w': 100000, 'c': ['{0}_r1_c1'.format(name), '{0}_r2_c2'.format(name)]}], n)
    arff.write_arff(header, ds, 'imbalanced/dynamic/{0}_RC_W100k'.format(name), root_dir)
    ds = create_drifting_stream(streams, [{'p': p1, 'w': 20000, 'c': ['{0}_r1_c1'.format(name), '{0}_r3_c2'.format(name)]}], n)
    arff.write_arff(header, ds, 'imbalanced/dynamic/{0}_RB_W20k'.format(name), root_dir)
    ds = create_drifting_stream(streams, [{'p': p1, 'w': 20000, 'c': ['{0}_r1_c1'.format(name), '{0}_r3_c2'.format(name)]}], n)
    arff.write_arff(header, ds, 'imbalanced/dynamic/{0}_RBC_W20k'.format(name), root_dir)

    ds = create_drifting_stream(streams, [
        {'p': p2, 'w': 10000, 'c': ['{0}_r1_c1'.format(name), '{0}_r2_c1'.format(name)]},
        {'p': p2 + d, 'w': 10000, 'c': ['{0}_r2_c1'.format(name), '{0}_r1_c1'.format(name)]}], n)
    arff.write_arff(header, ds, 'imbalanced/dynamic/{0}_RS_W10k'.format(name), root_dir)
    ds = create_drifting_stream(streams, [
        {'p': p2, 'w': 10000, 'c': ['{0}_r1_c1'.format(name), '{0}_r2_c2'.format(name)]},
        {'p': p2 + d, 'w': 10000, 'c': ['{0}_r2_c2'.format(name), '{0}_r1_c1'.format(name)]}], n)
    arff.write_arff(header, ds, 'imbalanced/dynamic/{0}_RSC_W10k'.format(name), root_dir)


def create_synth_drifting_streams():
    create_multiple_synth_drifting_streams('SEA', 250000, 200000, 50000, 500000)
    create_multiple_synth_drifting_streams('STAGGER', 250000, 200000, 50000, 500000)
    create_multiple_synth_drifting_streams('SINE', 250000, 200000, 50000, 500000)
    create_multiple_synth_drifting_streams('TREE', 250000, 200000, 100000, 500000)
    create_multiple_synth_drifting_streams('RBF', 250000, 200000, 100000, 500000)


def create_semi_imbalanced_drifting_streams():
    arff_data = arff.load_arff('real/ACTIVITY/TRANSFORMED/ACTIVITY', root_dir, False)
    ds = create_real_drifting_stream(arff_data['data'], [
        {'p': 5000, 'w': 100, 'c': ['m1', 'm2']}], {
            'm1': {'Walking': '1', 'Jogging': '1', 'Stairs': '2', 'Upstairs': '2', 'Downstairs': '2', 'Sitting': '4', 'Standing': '3', 'LyingDown': '4'},
            'm2': {'Walking': '3', 'Jogging': '2', 'Stairs': '1', 'Upstairs': '1', 'Downstairs': '1', 'Sitting': '3', 'Standing': '4', 'LyingDown': '4'}
    }, 10853)
    arff.write_arff(replace_classes(arff_data['attributes'], ['1', '2', '3', '4']), ds, 'imbalanced/dynamic/semi-synth/ACTIVITY-D1', root_dir)

    arff_data = arff.load_arff('real/ACTIVITY/RAW/ACTIVITY_RAW', root_dir, False)
    ds = create_real_drifting_stream(arff_data['data'], [
        {'p': 500000, 'w': 10000, 'c': ['m1', 'm2']}], {
            'm1': {'Walking': '1', 'Jogging': '1', 'Upstairs': '2', 'Downstairs': '2', 'Sitting': '3', 'Standing': '4'},
            'm2': {'Walking': '4', 'Jogging': '4', 'Upstairs': '3', 'Downstairs': '3', 'Sitting': '1', 'Standing': '2'}
    }, 1048570)
    arff.write_arff(replace_classes(arff_data['attributes'], ['1', '2', '3', '4']), ds, 'imbalanced/dynamic/semi-synth/ACTIVITY_RAW-D1', root_dir)

    arff_data = arff.load_arff('real/CONNECT4/CONNECT4', root_dir, False)
    ds = create_real_drifting_stream(arff_data['data'], [
        {'p': 20000, 'w': 100, 'c': ['m1', 'm2']}, {'p': 50000, 'w': 100, 'c': ['m2', 'm3']}], {
            'm1': {'win': '1', 'loss': 2, 'draw': 3},
            'm2': {'win': '3', 'loss': 2, 'draw': 1},
            'm3': {'win': '2', 'loss': 1, 'draw': 3}
    }, 67557)
    arff.write_arff(replace_classes(arff_data['attributes'], ['1', '2', '3']), ds, 'imbalanced/dynamic/semi-synth/CONNECT4-D1', root_dir)

    arff_data = arff.load_arff('real/COVERTYPE/COVERTYPE', root_dir, False)
    ds = create_real_drifting_stream(arff_data['data'], [
        {'p': 250000, 'w': 10000, 'c': ['m1', 'm2']}], {
            'm1': {'1': '1', '2': '2', '3': '3', '4': '3', '5': '3', '6': '4', '7': '4'},
            'm2': {'1': '4', '2': '4', '3': '2', '4': '3', '5': '3', '6': '1', '7': '1'}
    }, 581012)
    arff.write_arff(replace_classes(arff_data['attributes'], ['1', '2', '3', '4']), ds, 'imbalanced/dynamic/semi-synth/COVERTYPE-D1', root_dir)

    arff_data = arff.load_arff('real/DJ30/DJ30', root_dir, False)
    ds = create_real_drifting_stream(arff_data['data'], [
        {'p': 40000, 'w': 1000, 'c': ['m1', 'm2']}, {'p': 80000, 'w': 1000, 'c': ['m2', 'm3']}], {
            'm1': {'aa': '1', 'axp': '1', 'bs': '1', 'cat': '1', 'ci': '1', 'co': '1', 'dd': '1', 'dis': '1',
                   'ek': '1', 'ge': '1', 'gm': '1', 'hd': '1', 'hon': '1', 'hpq': '1', 'ibm': '1', 'intc': '1',
                   'ip': '1', 'jnj': '1', 'jpm': '1', 'mcd': '1', 'mmm': '2', 'mo': '2', 'mrk': '2', 'msft': '2',
                   'pg': '2', 'sbc': '2', 't': '3', 'utx': '3', 'wmt': '4', 'xom': '4'},
            'm2': {'aa': '4', 'axp': '4', 'bs': '4', 'cat': '4', 'ci': '4', 'co': '4', 'dd': '4', 'dis': '4',
                   'ek': '4', 'ge': '4', 'gm': '4', 'hd': '4', 'hon': '4', 'hpq': '4', 'ibm': '4', 'intc': '4',
                   'ip': '4', 'jnj': '4', 'jpm': '4', 'mcd': '4', 'mmm': '3', 'mo': '3', 'mrk': '3', 'msft': '3',
                   'pg': '3', 'sbc': '3', 't': '2', 'utx': '2', 'wmt': '1', 'xom': '1'},
            'm3': {'aa': '2', 'axp': '2', 'bs': '2', 'cat': '2', 'ci': '2', 'co': '2', 'dd': '2', 'dis': '2',
                   'ek': '2', 'ge': '2', 'gm': '2', 'hd': '2', 'hon': '2', 'hpq': '2', 'ibm': '2', 'intc': '2',
                   'ip': '2', 'jnj': '2', 'jpm': '2', 'mcd': '2', 'mmm': '1', 'mo': '1', 'mrk': '1', 'msft': '1',
                   'pg': '1', 'sbc': '1', 't': '4', 'utx': '4', 'wmt': '3', 'xom': '3'},
    }, 138166)
    arff.write_arff(replace_classes(arff_data['attributes'], ['1', '2', '3', '4']), ds, 'imbalanced/dynamic/semi-synth/DJ30-D1', root_dir)

    arff_data = arff.load_arff('real/GAS/GAS', root_dir, False)
    ds = create_real_drifting_stream(arff_data['data'], [
        {'p': 7000, 'w': 100, 'c': ['m1', 'm2']}], {
            'm1': {'1': '1', '2': '1', '3': '1', '4': '2', '5': '1', '6': '3'},
            'm2': {'1': '3', '2': '1', '3': '2', '4': '3', '5': '3', '6': '3'}
    }, 13910)
    arff.write_arff(replace_classes(arff_data['attributes'], ['1', '2', '3']), ds, 'imbalanced/dynamic/semi-synth/GAS-D1', root_dir)

    arff_data = arff.load_arff('real/SENSOR/SENSOR', root_dir, False)
    ds = create_real_drifting_stream(arff_data['data'], [
        {'p': 700000, 'w': 10000, 'c': ['m1', 'm2']}, {'p': 1400000, 'w': 10000, 'c': ['m2', 'm3']}], {
            'm1': {'1': '1', '2': '1', '3': '1', '4': '1', '5': '1', '6': '1', '7': '1', '8': '1', '9': '1', '10': '1',
                   '11': '1', '12': '1', '13': '1', '14': '1', '15': '1', '16': '1', '17': '1', '18': '1', '19': '1', '20': '1',
                   '21': '1', '22': '1', '23': '1', '24': '1', '25': '1', '26': '1', '27': '1', '28': '1', '29': '1', '30': '1',
                   '31': '1', '32': '1', '33': '1', '34': '1', '35': '1', '36': '1', '37': '1', '38': '1', '39': '1', '40': '1',
                   '41': '2', '42': '2', '43': '2', '44': '2', '45': '2', '46': '2', '47': '2', '48': '2', '49': '2', '50': '2',
                   '51': '3', '52': '3', '53': '3', '54': '3', '55': '4', '56': '4', '57': '4', '58': '4'},
            'm2': {'1': '4', '2': '4', '3': '4', '4': '4', '5': '4', '6': '4', '7': '4', '8': '4', '9': '4', '10': '4',
                   '11': '4', '12': '4', '13': '4', '14': '4', '15': '4', '16': '4', '17': '4', '18': '4', '19': '4', '20': '4',
                   '21': '4', '22': '4', '23': '4', '24': '4', '25': '4', '26': '4', '27': '4', '28': '4', '29': '4', '30': '4',
                   '31': '4', '32': '4', '33': '4', '34': '4', '35': '4', '36': '4', '37': '4', '38': '4', '39': '4', '40': '4',
                   '41': '3', '42': '3', '43': '3', '44': '3', '45': '3', '46': '3', '47': '3', '48': '3', '49': '3', '50': '3',
                   '51': '2', '52': '2', '53': '2', '54': '2', '55': '1', '56': '1', '57': '1', '58': '1'},
            'm3': {'1': '2', '2': '2', '3': '2', '4': '2', '5': '2', '6': '2', '7': '2', '8': '2', '9': '2', '10': '2',
                   '11': '2', '12': '2', '13': '2', '14': '2', '15': '2', '16': '2', '17': '2', '18': '2', '19': '2', '20': '2',
                   '21': '2', '22': '2', '23': '2', '24': '2', '25': '2', '26': '2', '27': '2', '28': '2', '29': '2', '30': '2',
                   '31': '2', '32': '2', '33': '2', '34': '2', '35': '2', '36': '2', '37': '2', '38': '2', '39': '2', '40': '2',
                   '41': '1', '42': '1', '43': '1', '44': '1', '45': '1', '46': '1', '47': '1', '48': '1', '49': '1', '50': '1',
                   '51': '4', '52': '4', '53': '4', '54': '4', '55': '3', '56': '3', '57': '3', '58': '3'},
    }, 2219802)
    arff.write_arff(replace_classes(arff_data['attributes'], ['1', '2', '3', '4']), ds, 'imbalanced/dynamic/semi-synth/SENSOR-D1', root_dir)

    arff_data = arff.load_arff('real/SPAM/SPAM09/SPAM', root_dir, False)
    ds = create_real_drifting_stream(arff_data['data'], [
        {'p': 4500, 'w': 100, 'c': ['m1', 'm2']}], {
            'm1': {'spam': '1', 'legitimate': '2'},
            'm2': {'spam': '2', 'legitimate': '1'}
    }, 9324)
    arff.write_arff(replace_classes(arff_data['attributes'], ['1', '2']), ds, 'imbalanced/dynamic/semi-synth/SPAM-D1', root_dir)

    arff_data = arff.load_arff('real/WEATHER/WEATHER', root_dir, False)
    ds = create_real_drifting_stream(arff_data['data'], [
        {'p': 6000, 'w': 1000, 'c': ['m1', 'm2']}, {'p': 12000, 'w': 1000, 'c': ['m2', 'm1']}], {
            'm1': {'1': '1', '2': '2'},
            'm2': {'1': '2', '2': '1'}
    }, 18158)
    arff.write_arff(replace_classes(arff_data['attributes'], ['1', '2']), ds, 'imbalanced/dynamic/semi-synth/WEATHER-D1', root_dir)

    arff_data = arff.load_arff('real/POKER/POKER', root_dir, False)
    ds = create_real_drifting_stream(arff_data['data'], [
        {'p': 300000, 'w': 10000, 'c': ['m1', 'm2']}, {'p': 600000, 'w': 10000, 'c': ['m2', 'm3']}], {
            'm1': {'0': '1', '1': '1', '2': '2', '3': '2', '4': '3', '5': '3', '6': '4', '7': '4', '8': '3', '9': '4'},
            'm2': {'0': '4', '1': '4', '2': '3', '3': '3', '4': '2', '5': '2', '6': '1', '7': '1', '8': '2', '9': '1'},
            'm3': {'0': '2', '1': '2', '2': '1', '3': '1', '4': '4', '5': '4', '6': '3', '7': '3', '8': '4', '9': '3'}
    }, 829201)
    arff.write_arff(replace_classes(arff_data['attributes'], ['1', '2', '3', '4']), ds, 'imbalanced/dynamic/semi-synth/POKER-D1', root_dir)

    arff_data = arff.load_arff('real/OLYMPIC/OLYMPIC', root_dir, False)
    ds = create_real_drifting_stream(arff_data['data'], [
        {'p': 90000, 'w': 1000, 'c': ['m1', 'm2']}, {'p': 180000, 'w': 1000, 'c': ['m2', 'm3']}], {
            'm1': {'None': '1', 'Bronze': '2', 'Silver': '2', 'Gold': '3'},
            'm2': {'None': '2', 'Bronze': '1', 'Silver': '3', 'Gold': '2'},
            'm3': {'None': '3', 'Bronze': '1', 'Silver': '1', 'Gold': '2'}
    }, 271116)
    arff.write_arff(replace_classes(arff_data['attributes'], ['1', '2', '3']), ds, 'imbalanced/dynamic/semi-synth/OLYMPIC-D1', root_dir)

    arff_data = arff.load_arff('real/TAGS/TAGS', root_dir, False)
    ds = create_real_drifting_stream(arff_data['data'], [
        {'p': 50000, 'w': 1000, 'c': ['m1', 'm2']}, {'p': 100000, 'w': 1000, 'c': ['m2', 'm3']}], {
            'm1': {'walking': '1', 'falling': '4', 'lying_down': '3', 'lying': '1', 'sitting_down': '4', 'sitting': '1',
                   'standing_up_from_lying': '2', 'on_all_fours': '3', 'sitting_on_the_ground': '2', 'standing_up_from_sitting': '4',
                   'standing_up_from_sitting_on_the_ground': '4'},
            'm2': {'walking': '4', 'falling': '1', 'lying_down': '2', 'lying': '4', 'sitting_down': '1', 'sitting': '4',
                   'standing_up_from_lying': '3', 'on_all_fours': '2', 'sitting_on_the_ground': '3', 'standing_up_from_sitting': '1',
                   'standing_up_from_sitting_on_the_ground': '1'},
            'm3': {'walking': '2', 'falling': '1', 'lying_down': '4', 'lying': '2', 'sitting_down': '3', 'sitting': '2',
                   'standing_up_from_lying': '1', 'on_all_fours': '4', 'sitting_on_the_ground': '1', 'standing_up_from_sitting': '3',
                   'standing_up_from_sitting_on_the_ground': '3'}
    }, 164860)
    arff.write_arff(replace_classes(arff_data['attributes'], ['1', '2', '3', '4']), ds, 'imbalanced/dynamic/semi-synth/TAGS-D1', root_dir)

    arff_data = arff.load_arff('real/CRIMES/CRIMES', root_dir, False)
    ds = create_real_drifting_stream(arff_data['data'], [
        {'p': 300000, 'w': 10000, 'c': ['m1', 'm2']}, {'p': 600000, 'w': 10000, 'c': ['m2', 'm3']}], {
            'm1': {'WARRANTS': '2', 'OTHER_OFFENSES': '1', 'LARCENY': '1', 'VEHICLE_THEFT': '2', 'VANDALISM': '2',
                   'NON-CRIMINAL': '1', 'ROBBERY': '3', 'ASSAULT': '1', 'WEAPON_LAWS': '4', 'BURGLARY': '3',
                   'SUSPICIOUS_OCC': '3', 'DRUNKENNESS': '4', 'FORGERY': '4', 'DRUG': '2', 'STOLEN_PROPERTY': '4',
                   'SECONDARY_CODES': '4', 'TRESPASS': '4', 'MISSING_PERSON': '3', 'FRAUD': '4', 'KIDNAPPING': '4',
                   'RUNAWAY': '4', 'DRIVING_UNDER_INFLUENCE': '4', 'SEX_OFFENSES_FORCIBLE': '4', 'PROSTITUTION': '4',
                   'DISORDERLY_CONDUCT': '4', 'ARSON': '4', 'FAMILY_OFFENSES': '4', 'LIQUOR_LAWS': '4', 'BRIBERY': '4',
                   'EMBEZZLEMENT': '4', 'SUICIDE': '4', 'LOITERING': '4', 'SEX_OFFENSES_NON_FORCIBLE': '4', 'EXTORTION': '4',
                   'GAMBLING': '4', 'BAD_CHECKS': '4', 'TREA': '4', 'RECOVERED_VEHICLE': '4', 'PORNOGRAPHY': '4'},
            'm2': {'WARRANTS': '4', 'OTHER_OFFENSES': '3', 'LARCENY': '3', 'VEHICLE_THEFT': '4', 'VANDALISM': '4',
                   'NON-CRIMINAL': '3', 'ROBBERY': '1', 'ASSAULT': '3', 'WEAPON_LAWS': '2', 'BURGLARY': '1',
                   'SUSPICIOUS_OCC': '1', 'DRUNKENNESS': '2', 'FORGERY': '2', 'DRUG': '4', 'STOLEN_PROPERTY': '2',
                   'SECONDARY_CODES': '2', 'TRESPASS': '2', 'MISSING_PERSON': '1', 'FRAUD': '2', 'KIDNAPPING': '2',
                   'RUNAWAY': '2', 'DRIVING_UNDER_INFLUENCE': '2', 'SEX_OFFENSES_FORCIBLE': '2', 'PROSTITUTION': '2',
                   'DISORDERLY_CONDUCT': '2', 'ARSON': '2', 'FAMILY_OFFENSES': '2', 'LIQUOR_LAWS': '2', 'BRIBERY': '2',
                   'EMBEZZLEMENT': '2', 'SUICIDE': '2', 'LOITERING': '2', 'SEX_OFFENSES_NON_FORCIBLE': '2', 'EXTORTION': '2',
                   'GAMBLING': '2', 'BAD_CHECKS': '2', 'TREA': '2', 'RECOVERED_VEHICLE': '2', 'PORNOGRAPHY': '2'},
            'm3': {'WARRANTS': '1', 'OTHER_OFFENSES': '2', 'LARCENY': '2', 'VEHICLE_THEFT': '1', 'VANDALISM': '1',
                   'NON-CRIMINAL': '2', 'ROBBERY': '4', 'ASSAULT': '2', 'WEAPON_LAWS': '3', 'BURGLARY': '4',
                   'SUSPICIOUS_OCC': '4', 'DRUNKENNESS': '3', 'FORGERY': '3', 'DRUG': '1', 'STOLEN_PROPERTY': '3',
                   'SECONDARY_CODES': '3', 'TRESPASS': '3', 'MISSING_PERSON': '4', 'FRAUD': '3', 'KIDNAPPING': '3',
                   'RUNAWAY': '3', 'DRIVING_UNDER_INFLUENCE': '3', 'SEX_OFFENSES_FORCIBLE': '3', 'PROSTITUTION': '3',
                   'DISORDERLY_CONDUCT': '3', 'ARSON': '3', 'FAMILY_OFFENSES': '3', 'LIQUOR_LAWS': '3', 'BRIBERY': '3',
                   'EMBEZZLEMENT': '3', 'SUICIDE': '3', 'LOITERING': '3', 'SEX_OFFENSES_NON_FORCIBLE': '3', 'EXTORTION': '3',
                   'GAMBLING': '3', 'BAD_CHECKS': '3', 'TREA': '3', 'RECOVERED_VEHICLE': '3', 'PORNOGRAPHY': '3'}
    }, 878049)
    arff.write_arff(replace_classes(arff_data['attributes'], ['1', '2', '3', '4']), ds, 'imbalanced/dynamic/semi-synth/CRIMES-D1', root_dir)

    arff_data = arff.load_arff('real/ELEC/ELEC', root_dir, False)
    ds = create_real_drifting_stream(arff_data['data'], [
        {'p': 20000, 'w': 500, 'c': ['m1', 'm2']}], {
                                         'm1': {'UP': '1', 'DOWN': '2'},
                                         'm2': {'UP': '2', 'DOWN': '1'}
                                     }, 45312)
    arff.write_arff(replace_classes(arff_data['attributes'], ['1', '2']), ds, 'imbalanced/dynamic/semi-synth/ELEC-D1', root_dir)


def create_semi_drifting_streams():
    # arff_data = arff.load_arff('real/ACTIVITY/TRANSFORMED/ACTIVITY', root_dir, False)
    # concepts = {
    #     'm1': {'Walking': '1', 'Jogging': '2', 'Stairs': '3', 'Upstairs': '4', 'Downstairs': '5', 'Sitting': '6',
    #            'Standing': '7', 'LyingDown': '8'},
    #     'm2': {'Walking': '7', 'Jogging': '8', 'Stairs': '1', 'Upstairs': '2', 'Downstairs': '3', 'Sitting': '4',
    #            'Standing': '5', 'LyingDown': '6'},
    #     'm3': {'Walking': '5', 'Jogging': '6', 'Stairs': '7', 'Upstairs': '8', 'Downstairs': '1', 'Sitting': '2',
    #            'Standing': '3', 'LyingDown': '4'}
    # }
    # classes = [str(i) for i in range(1, 8)]
    #
    # ds = create_real_drifting_stream(arff_data['data'], [{'p': 3000, 'w': 10, 'c': ['m1', 'm2']},
    #                                                      {'p': 6000, 'w': 10, 'c': ['m2', 'm3']}], concepts, 10853)
    # arff.write_arff(replace_classes(arff_data['attributes'], classes), ds, 'semi-synth/ACTIVITY-SUD', root_dir)
    # ds = create_real_drifting_stream(arff_data['data'], [{'p': 3000, 'w': 500, 'c': ['m1', 'm2']},
    #                                                      {'p': 6000, 'w': 500, 'c': ['m2', 'm3']}], concepts, 10853)
    # arff.write_arff(replace_classes(arff_data['attributes'], classes), ds, 'semi-synth/ACTIVITY-MED', root_dir)
    # ds = create_real_drifting_stream(arff_data['data'], [{'p': 3000, 'w': 2000, 'c': ['m1', 'm2']},
    #                                                      {'p': 6000, 'w': 2000, 'c': ['m2', 'm3']}], concepts, 10853)
    # arff.write_arff(replace_classes(arff_data['attributes'], classes), ds, 'semi-synth/ACTIVITY-LNG', root_dir)
    #
    # arff_data = arff.load_arff('real/ACTIVITY/RAW/ACTIVITY_RAW', root_dir, False)
    # concepts = {
    #     'm1': {'Walking': '1', 'Jogging': '2', 'Upstairs': '3', 'Downstairs': '4', 'Sitting': '5', 'Standing': '6'},
    #     'm2': {'Walking': '5', 'Jogging': '6', 'Upstairs': '1', 'Downstairs': '2', 'Sitting': '3', 'Standing': '4'},
    #     'm3': {'Walking': '3', 'Jogging': '4', 'Upstairs': '5', 'Downstairs': '6', 'Sitting': '1', 'Standing': '2'}
    # }
    # classes = [str(i) for i in range(1, 6)]
    #
    # ds = create_real_drifting_stream(arff_data['data'], [{'p': 300000, 'w': 100, 'c': ['m1', 'm2']},
    #                                                      {'p': 600000, 'w': 100, 'c': ['m2', 'm3']}], concepts, 1048570)
    # arff.write_arff(replace_classes(arff_data['attributes'], classes), ds, 'semi-synth/ACTIVITY_RAW-SUD', root_dir)
    # ds = create_real_drifting_stream(arff_data['data'], [{'p': 300000, 'w': 50000, 'c': ['m1', 'm2']},
    #                                                      {'p': 600000, 'w': 50000, 'c': ['m2', 'm3']}], concepts, 1048570)
    # arff.write_arff(replace_classes(arff_data['attributes'], classes), ds, 'semi-synth/ACTIVITY_RAW-MED', root_dir)
    # ds = create_real_drifting_stream(arff_data['data'], [{'p': 300000, 'w': 200000, 'c': ['m1', 'm2']},
    #                                                      {'p': 600000, 'w': 200000, 'c': ['m2', 'm3']}], concepts, 1048570)
    # arff.write_arff(replace_classes(arff_data['attributes'], classes), ds, 'semi-synth/ACTIVITY_RAW-LNG', root_dir)
    #
    # arff_data = arff.load_arff('real/CONNECT4/CONNECT4', root_dir, False)
    # concepts = {
    #     'm1': {'win': '1', 'loss': 2, 'draw': 3},
    #     'm2': {'win': '3', 'loss': 1, 'draw': 2},
    #     'm3': {'win': '2', 'loss': 3, 'draw': 1},
    # }
    # classes = [str(i) for i in range(1, 3)]
    #
    # ds = create_real_drifting_stream(arff_data['data'], [{'p': 20000, 'w': 100, 'c': ['m1', 'm2']},
    #                                                      {'p': 40000, 'w': 100, 'c': ['m2', 'm3']}], concepts, 67557)
    # arff.write_arff(replace_classes(arff_data['attributes'], classes), ds, 'semi-synth/CONNECT4-SUD', root_dir)
    # ds = create_real_drifting_stream(arff_data['data'], [{'p': 20000, 'w': 5000, 'c': ['m1', 'm2']},
    #                                                      {'p': 40000, 'w': 5000, 'c': ['m2', 'm3']}], concepts, 67557)
    # arff.write_arff(replace_classes(arff_data['attributes'], classes), ds, 'semi-synth/CONNECT4-MED', root_dir)
    # ds = create_real_drifting_stream(arff_data['data'], [{'p': 20000, 'w': 15000, 'c': ['m1', 'm2']},
    #                                                      {'p': 45000, 'w': 15000, 'c': ['m2', 'm3']}], concepts, 67557)
    # arff.write_arff(replace_classes(arff_data['attributes'], classes), ds, 'semi-synth/CONNECT4-LNG', root_dir)
    #
    # arff_data = arff.load_arff('real/COVERTYPE/COVERTYPE', root_dir, False)
    # concepts = {
    #     'm1': {'1': '1', '2': '2', '3': '3', '4': '4', '5': '5', '6': '6', '7': '7'},
    #     'm2': {'1': '6', '2': '7', '3': '1', '4': '2', '5': '3', '6': '4', '7': '5'},
    #     'm3': {'1': '4', '2': '5', '3': '6', '4': '7', '5': '1', '6': '2', '7': '3'},
    # }
    # classes = [str(i) for i in range(1, 7)]
    #
    # ds = create_real_drifting_stream(arff_data['data'], [{'p': 180000, 'w': 100, 'c': ['m1', 'm2']},
    #                                                      {'p': 350000, 'w': 100, 'c': ['m2', 'm3']}], concepts, 581012)
    # arff.write_arff(replace_classes(arff_data['attributes'], classes), ds, 'semi-synth/COVERTYPE-SUD', root_dir)
    # ds = create_real_drifting_stream(arff_data['data'], [{'p': 180000, 'w': 30000, 'c': ['m1', 'm2']},
    #                                                      {'p': 350000, 'w': 30000, 'c': ['m2', 'm3']}], concepts, 581012)
    # arff.write_arff(replace_classes(arff_data['attributes'], classes), ds, 'semi-synth/COVERTYPE-MED', root_dir)
    # ds = create_real_drifting_stream(arff_data['data'], [{'p': 180000, 'w': 100000, 'c': ['m1', 'm2']},
    #                                                      {'p': 350000, 'w': 100000, 'c': ['m2', 'm3']}], concepts, 581012)
    # arff.write_arff(replace_classes(arff_data['attributes'], classes), ds, 'semi-synth/COVERTYPE-LNG', root_dir)
    #
    # arff_data = arff.load_arff('real/DJ30/DJ30', root_dir, False)
    # concepts = {
    #     'm1': {'aa': '1', 'axp': '2', 'bs': '3', 'cat': '4', 'ci': '5', 'co': '6', 'dd': '7', 'dis': '8', 'ek': '9',
    #            'ge': '10', 'gm': '11', 'hd': '12', 'hon': '13', 'hpq': '14', 'ibm': '15', 'intc': '16', 'ip': '17',
    #            'jnj': '18', 'jpm': '19', 'mcd': '20', 'mmm': '21', 'mo': '22', 'mrk': '23', 'msft': '24', 'pg': '25',
    #            'sbc': '26', 't': '27', 'utx': '28', 'wmt': '29', 'xom': '30'},
    #     'm2': {'aa': '29', 'axp': '30', 'bs': '1', 'cat': '2', 'ci': '3', 'co': '4', 'dd': '5', 'dis': '6', 'ek': '7',
    #            'ge': '8', 'gm': '9', 'hd': '10', 'hon': '11', 'hpq': '12', 'ibm': '13', 'intc': '14', 'ip': '15',
    #            'jnj': '16', 'jpm': '17', 'mcd': '18', 'mmm': '19', 'mo': '20', 'mrk': '21', 'msft': '22', 'pg': '23',
    #            'sbc': '24', 't': '25', 'utx': '26', 'wmt': '27', 'xom': '28'},
    #     'm3': {'aa': '27', 'axp': '28', 'bs': '29', 'cat': '30', 'ci': '1', 'co': '2', 'dd': '3', 'dis': '4', 'ek': '5',
    #            'ge': '6', 'gm': '7', 'hd': '8', 'hon': '9', 'hpq': '10', 'ibm': '11', 'intc': '12', 'ip': '13',
    #            'jnj': '14', 'jpm': '15', 'mcd': '16', 'mmm': '17', 'mo': '18', 'mrk': '19', 'msft': '20', 'pg': '21',
    #            'sbc': '22', 't': '23', 'utx': '24', 'wmt': '25', 'xom': '26'}
    # }
    # classes = [str(i) for i in range(1, 30)]
    #
    # ds = create_real_drifting_stream(arff_data['data'], [{'p': 40000, 'w': 100, 'c': ['m1', 'm2']},
    #                                                      {'p': 80000, 'w': 100, 'c': ['m2', 'm3']}], concepts, 138166)
    # arff.write_arff(replace_classes(arff_data['attributes'], classes), ds, 'semi-synth/DJ30-SUD', root_dir)
    # ds = create_real_drifting_stream(arff_data['data'], [{'p': 40000, 'w': 7000, 'c': ['m1', 'm2']},
    #                                                      {'p': 80000, 'w': 7000, 'c': ['m2', 'm3']}], concepts, 138166)
    # arff.write_arff(replace_classes(arff_data['attributes'], classes), ds, 'semi-synth/DJ30-MED', root_dir)
    # ds = create_real_drifting_stream(arff_data['data'], [{'p': 40000, 'w': 25000, 'c': ['m1', 'm2']},
    #                                                      {'p': 80000, 'w': 25000, 'c': ['m2', 'm3']}], concepts, 138166)
    # arff.write_arff(replace_classes(arff_data['attributes'], classes), ds, 'semi-synth/DJ30-LNG', root_dir)
    #
    # arff_data = arff.load_arff('real/GAS/GAS', root_dir, False)
    # concepts = {
    #     'm1': {'1': '1', '2': '2', '3': '3', '4': '4', '5': '5', '6': '6'},
    #     'm2': {'1': '5', '2': '6', '3': '1', '4': '2', '5': '3', '6': '4'},
    #     'm3': {'1': '3', '2': '4', '3': '5', '4': '6', '5': '1', '6': '2'},
    # }
    # classes = [str(i) for i in range(1, 6)]
    #
    # ds = create_real_drifting_stream(arff_data['data'], [{'p': 4000, 'w': 100, 'c': ['m1', 'm2']},
    #                                                      {'p': 8000, 'w': 100, 'c': ['m2', 'm3']}], concepts, 13910)
    # arff.write_arff(replace_classes(arff_data['attributes'], classes), ds, 'semi-synth/GAS-SUD', root_dir)
    # ds = create_real_drifting_stream(arff_data['data'], [{'p': 4000, 'w': 700, 'c': ['m1', 'm2']},
    #                                                      {'p': 8000, 'w': 700, 'c': ['m2', 'm3']}], concepts, 13910)
    # arff.write_arff(replace_classes(arff_data['attributes'], classes), ds, 'semi-synth/GAS-MED', root_dir)
    # ds = create_real_drifting_stream(arff_data['data'], [{'p': 4000, 'w': 2500, 'c': ['m1', 'm2']},
    #                                                      {'p': 8000, 'w': 2500, 'c': ['m2', 'm3']}], concepts, 13910)
    # arff.write_arff(replace_classes(arff_data['attributes'], classes), ds, 'semi-synth/GAS-LNG', root_dir)
    #
    # arff_data = arff.load_arff('real/SENSOR/SENSOR', root_dir, False)
    # concepts = {
    #     'm1': {'1': '1', '2': '2', '3': '3', '4': '4', '5': '5', '6': '6', '7': '7', '8': '8', '9': '9', '10': '10',
    #            '11': '11', '12': '12', '13': '13', '14': '14', '15': '15', '16': '16', '17': '17', '18': '18', '19': '19',
    #            '20': '20', '21': '21', '22': '22', '23': '23', '24': '24', '25': '25', '26': '26', '27': '27', '28': '28',
    #            '29': '29', '30': '30', '31': '31', '32': '32', '33': '33', '34': '34', '35': '35', '36': '36', '37': '37',
    #            '38': '38', '39': '39', '40': '40', '41': '41', '42': '42', '43': '43', '44': '44', '45': '45', '46': '46',
    #            '47': '47', '48': '48', '49': '49', '50': '50', '51': '51', '52': '52', '53': '53', '54': '54', '55': '55',
    #            '56': '56', '57': '57', '58': '58'},
    #     'm2': {'1': '48', '2': '49', '3': '50', '4': '51', '5': '52', '6': '53', '7': '54', '8': '55', '9': '56', '10': '57',
    #            '11': '58', '12': '1', '13': '2', '14': '3', '15': '4', '16': '5', '17': '6', '18': '7', '19': '8',
    #            '20': '9', '21': '10', '22': '11', '23': '12', '24': '13', '25': '14', '26': '15', '27': '16', '28': '17',
    #            '29': '18', '30': '19', '31': '20', '32': '21', '33': '22', '34': '23', '35': '24', '36': '25', '37': '26',
    #            '38': '27', '39': '28', '40': '29', '41': '30', '42': '31', '43': '32', '44': '33', '45': '34', '46': '35',
    #            '47': '36', '48': '37', '49': '38', '50': '39', '51': '40', '52': '41', '53': '42', '54': '43', '55': '44',
    #            '56': '45', '57': '46', '58': '47'},
    #     'm3': {'1': '38', '2': '39', '3': '40', '4': '41', '5': '42', '6': '43', '7': '44', '8': '45', '9': '46', '10': '47',
    #            '11': '48', '12': '49', '13': '50', '14': '51', '15': '52', '16': '53', '17': '54', '18': '55', '19': '56',
    #            '20': '57', '21': '58', '22': '1', '23': '2', '24': '3', '25': '4', '26': '5', '27': '6', '28': '7',
    #            '29': '8', '30': '9', '31': '10', '32': '11', '33': '12', '34': '13', '35': '14', '36': '15', '37': '16',
    #            '38': '17', '39': '18', '40': '19', '41': '20', '42': '21', '43': '22', '44': '23', '45': '24', '46': '25',
    #            '47': '26', '48': '27', '49': '28', '50': '29', '51': '30', '52': '31', '53': '32', '54': '33', '55': '34',
    #            '56': '35', '57': '36', '58': '37'},
    #
    # }
    # classes = [str(i) for i in range(1, 58)]
    #
    # ds = create_real_drifting_stream(arff_data['data'], [{'p': 650000, 'w': 100, 'c': ['m1', 'm2']},
    #                                                      {'p': 1400000, 'w': 100, 'c': ['m2', 'm3']}], concepts, 2219802)
    # arff.write_arff(replace_classes(arff_data['attributes'], classes), ds, 'semi-synth/SENSOR-SUD', root_dir)
    # ds = create_real_drifting_stream(arff_data['data'], [{'p': 650000, 'w': 100000, 'c': ['m1', 'm2']},
    #                                                      {'p': 1400000, 'w': 100000, 'c': ['m2', 'm3']}], concepts, 2219802)
    # arff.write_arff(replace_classes(arff_data['attributes'], classes), ds, 'semi-synth/SENSOR-MED', root_dir)
    # ds = create_real_drifting_stream(arff_data['data'], [{'p': 650000, 'w': 400000, 'c': ['m1', 'm2']},
    #                                                      {'p': 1400000, 'w': 400000, 'c': ['m2', 'm3']}], concepts, 2219802)
    # arff.write_arff(replace_classes(arff_data['attributes'], classes), ds, 'semi-synth/SENSOR-LNG', root_dir)
    #
    # arff_data = arff.load_arff('real/SPAM/SPAM09/SPAM', root_dir, False)
    # concepts = {
    #     'm1': {'spam': '1', 'legitimate': '2'},
    #     'm2': {'spam': '2', 'legitimate': '1'},
    # }
    # classes = [str(i) for i in range(1, 2)]
    #
    # ds = create_real_drifting_stream(arff_data['data'], [{'p': 2500, 'w': 10, 'c': ['m1', 'm2']},
    #                                                      {'p': 5500, 'w': 10, 'c': ['m2', 'm1']}], concepts, 9324)
    # arff.write_arff(replace_classes(arff_data['attributes'], classes), ds, 'semi-synth/SPAM-SUD', root_dir)
    # ds = create_real_drifting_stream(arff_data['data'], [{'p': 2500, 'w': 500, 'c': ['m1', 'm2']},
    #                                                      {'p': 5500, 'w': 500, 'c': ['m2', 'm1']}], concepts, 9324)
    # arff.write_arff(replace_classes(arff_data['attributes'], classes), ds, 'semi-synth/SPAM-MED', root_dir)
    # ds = create_real_drifting_stream(arff_data['data'], [{'p': 2500, 'w': 1800, 'c': ['m1', 'm2']},
    #                                                      {'p': 5500, 'w': 1800, 'c': ['m2', 'm1']}], concepts, 9324)
    # arff.write_arff(replace_classes(arff_data['attributes'], classes), ds, 'semi-synth/SPAM-LNG', root_dir)
    #
    # arff_data = arff.load_arff('real/WEATHER/WEATHER', root_dir, False)
    # concepts = {
    #     'm1': {'1': '1', '2': '2'},
    #     'm2': {'1': '2', '2': '1'},
    # }
    # classes = [str(i) for i in range(1, 2)]
    #
    # ds = create_real_drifting_stream(arff_data['data'], [{'p': 5500, 'w': 10, 'c': ['m1', 'm2']},
    #                                                      {'p': 10500, 'w': 10, 'c': ['m2', 'm1']}], concepts, 18158)
    # arff.write_arff(replace_classes(arff_data['attributes'], classes), ds, 'semi-synth/WEATHER-SUD', root_dir)
    # ds = create_real_drifting_stream(arff_data['data'], [{'p': 5500, 'w': 1000, 'c': ['m1', 'm2']},
    #                                                      {'p': 10500, 'w': 1000, 'c': ['m2', 'm1']}], concepts, 18158)
    # arff.write_arff(replace_classes(arff_data['attributes'], classes), ds, 'semi-synth/WEATHER-MED', root_dir)
    # ds = create_real_drifting_stream(arff_data['data'], [{'p': 5500, 'w': 3500, 'c': ['m1', 'm2']},
    #                                                      {'p': 10500, 'w': 3500, 'c': ['m2', 'm1']}], concepts, 18158)
    # arff.write_arff(replace_classes(arff_data['attributes'], classes), ds, 'semi-synth/WEATHER-LNG', root_dir)
    #
    # arff_data = arff.load_arff('real/POKER/POKER', root_dir, False)
    # concepts = {
    #     'm1': {'0': '1', '1': '2', '2': '3', '3': '4', '4': '5', '5': '6', '6': '7', '7': '8', '8': '9', '9': '10'},
    #     'm2': {'0': '9', '1': '10', '2': '1', '3': '2', '4': '3', '5': '4', '6': '5', '7': '6', '8': '7', '9': '8'},
    #     'm3': {'0': '7', '1': '8', '2': '9', '3': '10', '4': '1', '5': '2', '6': '3', '7': '4', '8': '5', '9': '6'},
    # }
    #
    # classes = [str(i) for i in range(1, 10)]
    # ds = create_real_drifting_stream(arff_data['data'], [{'p': 250000, 'w': 100, 'c': ['m1', 'm2']},
    #                                                      {'p': 500000, 'w': 100, 'c': ['m2', 'm3']}], concepts, 829201)
    # arff.write_arff(replace_classes(arff_data['attributes'], classes), ds, 'semi-synth/POKER-SUD', root_dir)
    # ds = create_real_drifting_stream(arff_data['data'], [{'p': 250000, 'w': 40000, 'c': ['m1', 'm2']},
    #                                                      {'p': 500000, 'w': 40000, 'c': ['m2', 'm3']}], concepts, 829201)
    # arff.write_arff(replace_classes(arff_data['attributes'], classes), ds, 'semi-synth/POKER-MED', root_dir)
    # ds = create_real_drifting_stream(arff_data['data'], [{'p': 250000, 'w': 160000, 'c': ['m1', 'm2']},
    #                                                      {'p': 500000, 'w': 160000, 'c': ['m2', 'm3']}], concepts, 829201)
    # arff.write_arff(replace_classes(arff_data['attributes'], classes), ds, 'semi-synth/POKER-LNG', root_dir)
    #
    # arff_data = arff.load_arff('real/OLYMPIC/OLYMPIC', root_dir, False)
    # classes = [str(i) for i in range(1, 4)]
    # concepts = {
    #     'm1': {'None': '1', 'Bronze': '2', 'Silver': '3', 'Gold': '4'},
    #     'm2': {'None': '4', 'Bronze': '1', 'Silver': '2', 'Gold': '3'},
    #     'm3': {'None': '3', 'Bronze': '4', 'Silver': '1', 'Gold': '2'},
    # }
    #
    # ds = create_real_drifting_stream(arff_data['data'], [{'p': 80000, 'w': 100, 'c': ['m1', 'm2']},
    #                                                      {'p': 160000, 'w': 100, 'c': ['m2', 'm3']}], concepts, 271116)
    # arff.write_arff(replace_classes(arff_data['attributes'], classes), ds, 'semi-synth/OLYMPIC-SUD', root_dir)
    # ds = create_real_drifting_stream(arff_data['data'], [{'p': 80000, 'w': 12000, 'c': ['m1', 'm2']},
    #                                                      {'p': 160000, 'w': 12000, 'c': ['m2', 'm3']}], concepts, 271116)
    # arff.write_arff(replace_classes(arff_data['attributes'], classes), ds, 'semi-synth/OLYMPIC-MED', root_dir)
    # ds = create_real_drifting_stream(arff_data['data'], [{'p': 80000, 'w': 50000, 'c': ['m1', 'm2']},
    #                                                      {'p': 160000, 'w': 50000, 'c': ['m2', 'm3']}], concepts, 271116)
    # arff.write_arff(replace_classes(arff_data['attributes'], classes), ds, 'semi-synth/OLYMPIC-LNG', root_dir)
    #
    # arff_data = arff.load_arff('real/TAGS/TAGS', root_dir, False)
    # concepts = {
    #     'm1': {'walking': '1', 'falling': '2', 'lying_down': '3', 'lying': '4', 'sitting_down': '5', 'sitting': '6',
    #            'standing_up_from_lying': '7', 'on_all_fours': '8', 'sitting_on_the_ground': '9',
    #            'standing_up_from_sitting': '10', 'standing_up_from_sitting_on_the_ground': '11'},
    #     'm2': {'walking': '9', 'falling': '10', 'lying_down': '11', 'lying': '1', 'sitting_down': '2', 'sitting': '3',
    #            'standing_up_from_lying': '4', 'on_all_fours': '5', 'sitting_on_the_ground': '6',
    #            'standing_up_from_sitting': '7', 'standing_up_from_sitting_on_the_ground': '8'},
    #     'm3': {'walking': '6', 'falling': '7', 'lying_down': '8', 'lying': '9', 'sitting_down': '10', 'sitting': '11',
    #            'standing_up_from_lying': '1', 'on_all_fours': '2', 'sitting_on_the_ground': '3',
    #            'standing_up_from_sitting': '4', 'standing_up_from_sitting_on_the_ground': '5'},
    # }
    # classes = [str(i) for i in range(1, 11)]
    #
    # ds = create_real_drifting_stream(arff_data['data'], [{'p': 50000, 'w': 100, 'c': ['m1', 'm2']},
    #                                                      {'p': 100000, 'w': 100, 'c': ['m2', 'm3']}], concepts, 164860)
    # arff.write_arff(replace_classes(arff_data['attributes'], classes), ds, 'semi-synth/TAGS-SUD', root_dir)
    # ds = create_real_drifting_stream(arff_data['data'], [{'p': 50000, 'w': 8000, 'c': ['m1', 'm2']},
    #                                                      {'p': 100000, 'w': 8000, 'c': ['m2', 'm3']}], concepts, 164860)
    # arff.write_arff(replace_classes(arff_data['attributes'], classes), ds, 'semi-synth/TAGS-MED', root_dir)
    # ds = create_real_drifting_stream(arff_data['data'], [{'p': 50000, 'w': 30000, 'c': ['m1', 'm2']},
    #                                                      {'p': 100000, 'w': 30000, 'c': ['m2', 'm3']}], concepts, 164860)
    # arff.write_arff(replace_classes(arff_data['attributes'], classes), ds, 'semi-synth/TAGS-LNG', root_dir)
    #
    # arff_data = arff.load_arff('real/CRIMES/CRIMES', root_dir, False)
    # concepts = {
    #     'm1': {'WARRANTS': '1', 'OTHER_OFFENSES': '2', 'LARCENY': '3', 'VEHICLE_THEFT': '4', 'VANDALISM': '5',
    #            'NON-CRIMINAL': '6', 'ROBBERY': '7', 'ASSAULT': '8', 'WEAPON_LAWS': '9', 'BURGLARY': '10',
    #            'SUSPICIOUS_OCC': '11', 'DRUNKENNESS': '12', 'FORGERY': '13', 'DRUG': '14', 'STOLEN_PROPERTY': '15',
    #            'SECONDARY_CODES': '16', 'TRESPASS': '17', 'MISSING_PERSON': '18', 'FRAUD': '19', 'KIDNAPPING': '20',
    #            'RUNAWAY': '21', 'DRIVING_UNDER_INFLUENCE': '22', 'SEX_OFFENSES_FORCIBLE': '23', 'PROSTITUTION': '24',
    #            'DISORDERLY_CONDUCT': '25', 'ARSON': '26', 'FAMILY_OFFENSES': '27', 'LIQUOR_LAWS': '28', 'BRIBERY': '29',
    #            'EMBEZZLEMENT': '30', 'SUICIDE': '31', 'LOITERING': '32', 'SEX_OFFENSES_NON_FORCIBLE': '33',
    #            'EXTORTION': '34', 'GAMBLING': '35', 'BAD_CHECKS': '36', 'TREA': '37', 'RECOVERED_VEHICLE': '38',
    #            'PORNOGRAPHY': '39'},
    #     'm2': {'WARRANTS': '35', 'OTHER_OFFENSES': '36', 'LARCENY': '37', 'VEHICLE_THEFT': '38', 'VANDALISM': '39',
    #            'NON-CRIMINAL': '1', 'ROBBERY': '2', 'ASSAULT': '3', 'WEAPON_LAWS': '4', 'BURGLARY': '5',
    #            'SUSPICIOUS_OCC': '6', 'DRUNKENNESS': '7', 'FORGERY': '8', 'DRUG': '9', 'STOLEN_PROPERTY': '10',
    #            'SECONDARY_CODES': '11', 'TRESPASS': '12', 'MISSING_PERSON': '13', 'FRAUD': '14', 'KIDNAPPING': '15',
    #            'RUNAWAY': '16', 'DRIVING_UNDER_INFLUENCE': '17', 'SEX_OFFENSES_FORCIBLE': '18', 'PROSTITUTION': '19',
    #            'DISORDERLY_CONDUCT': '20', 'ARSON': '21', 'FAMILY_OFFENSES': '22', 'LIQUOR_LAWS': '23', 'BRIBERY': '24',
    #            'EMBEZZLEMENT': '25', 'SUICIDE': '26', 'LOITERING': '27', 'SEX_OFFENSES_NON_FORCIBLE': '28',
    #            'EXTORTION': '29', 'GAMBLING': '30', 'BAD_CHECKS': '31', 'TREA': '32', 'RECOVERED_VEHICLE': '33',
    #            'PORNOGRAPHY': '34'},
    #     'm3': {'WARRANTS': '30', 'OTHER_OFFENSES': '31', 'LARCENY': '32', 'VEHICLE_THEFT': '33', 'VANDALISM': '34',
    #            'NON-CRIMINAL': '35', 'ROBBERY': '36', 'ASSAULT': '37', 'WEAPON_LAWS': '38', 'BURGLARY': '39',
    #            'SUSPICIOUS_OCC': '1', 'DRUNKENNESS': '2', 'FORGERY': '3', 'DRUG': '4', 'STOLEN_PROPERTY': '5',
    #            'SECONDARY_CODES': '6', 'TRESPASS': '7', 'MISSING_PERSON': '8', 'FRAUD': '9', 'KIDNAPPING': '10',
    #            'RUNAWAY': '11', 'DRIVING_UNDER_INFLUENCE': '12', 'SEX_OFFENSES_FORCIBLE': '13', 'PROSTITUTION': '14',
    #            'DISORDERLY_CONDUCT': '15', 'ARSON': '16', 'FAMILY_OFFENSES': '17', 'LIQUOR_LAWS': '18', 'BRIBERY': '19',
    #            'EMBEZZLEMENT': '20', 'SUICIDE': '21', 'LOITERING': '22', 'SEX_OFFENSES_NON_FORCIBLE': '23',
    #            'EXTORTION': '24', 'GAMBLING': '25', 'BAD_CHECKS': '26', 'TREA': '27', 'RECOVERED_VEHICLE': '28',
    #            'PORNOGRAPHY': '29'},
    # }
    # classes = [str(i) for i in range(1, 39)]
    #
    # ds = create_real_drifting_stream(arff_data['data'], [{'p': 260000, 'w': 100, 'c': ['m1', 'm2']},
    #                                                      {'p': 520000, 'w': 100, 'c': ['m2', 'm3']}], concepts, 878049)
    # arff.write_arff(replace_classes(arff_data['attributes'], classes), ds, 'semi-synth/CRIMES-SUD', root_dir)
    # ds = create_real_drifting_stream(arff_data['data'], [{'p': 260000, 'w': 45000, 'c': ['m1', 'm2']},
    #                                                      {'p': 520000, 'w': 45000, 'c': ['m2', 'm3']}], concepts, 878049)
    # arff.write_arff(replace_classes(arff_data['attributes'], classes), ds, 'semi-synth/CRIMES-MED', root_dir)
    # ds = create_real_drifting_stream(arff_data['data'], [{'p': 260000, 'w': 150000, 'c': ['m1', 'm2']},
    #                                                      {'p': 520000, 'w': 150000, 'c': ['m2', 'm3']}], concepts, 878049)
    # arff.write_arff(replace_classes(arff_data['attributes'], classes), ds, 'semi-synth/CRIMES-LNG', root_dir)
    #
    # arff_data = arff.load_arff('real/ELEC/ELEC', root_dir, False)
    # concepts = {
    #     'm1': {'UP': '1', 'DOWN': '2'},
    #     'm2': {'UP': '2', 'DOWN': '1'},
    # }
    # classes = [str(i) for i in range(1, 2)]
    #
    # ds = create_real_drifting_stream(arff_data['data'], [{'p': 14000, 'w': 100, 'c': ['m1', 'm2']},
    #                                                      {'p': 28000, 'w': 100, 'c': ['m2', 'm1']}], concepts, 45312)
    # arff.write_arff(replace_classes(arff_data['attributes'], classes), ds, 'semi-synth/ELEC-SUD', root_dir)
    # ds = create_real_drifting_stream(arff_data['data'], [{'p': 14000, 'w': 2500, 'c': ['m1', 'm2']},
    #                                                      {'p': 28000, 'w': 2500, 'c': ['m2', 'm1']}], concepts, 45312)
    # arff.write_arff(replace_classes(arff_data['attributes'], classes), ds, 'semi-synth/ELEC-MED', root_dir)
    # ds = create_real_drifting_stream(arff_data['data'], [{'p': 14000, 'w': 9000, 'c': ['m1', 'm2']},
    #                                                      {'p': 28000, 'w': 9000, 'c': ['m2', 'm1']}], concepts, 45312)
    # arff.write_arff(replace_classes(arff_data['attributes'], classes), ds, 'semi-synth/ELEC-LNG', root_dir)
    #
    # arff_data = arff.load_arff('real/AIRLINES/AIRLINES', root_dir, False)
    # concepts = {
    #     'm1': {'0': '1', '1': '2'},
    #     'm2': {'0': '2', '1': '1'},
    # }
    # classes = [str(i) for i in range(1, 2)]
    #
    # ds = create_real_drifting_stream(arff_data['data'], [{'p': 160000, 'w': 100, 'c': ['m1', 'm2']},
    #                                                      {'p': 320000, 'w': 100, 'c': ['m2', 'm1']}], concepts, 539382)
    # arff.write_arff(replace_classes(arff_data['attributes'], classes), ds, 'semi-synth/AIRLINES-SUD', root_dir)
    # ds = create_real_drifting_stream(arff_data['data'], [{'p': 160000, 'w': 30000, 'c': ['m1', 'm2']},
    #                                                      {'p': 320000, 'w': 30000, 'c': ['m2', 'm1']}], concepts, 539382)
    # arff.write_arff(replace_classes(arff_data['attributes'], classes), ds, 'semi-synth/AIRLINES-MED', root_dir)
    # ds = create_real_drifting_stream(arff_data['data'], [{'p': 160000, 'w': 100000, 'c': ['m1', 'm2']},
    #                                                      {'p': 320000, 'w': 100000, 'c': ['m2', 'm1']}], concepts, 539382)
    # arff.write_arff(replace_classes(arff_data['attributes'], classes), ds, 'semi-synth/AIRLINES-LNG', root_dir)

    arff_data = arff.load_arff('real/EEG/EEG', root_dir, False)
    concepts = {
        'm1': {'0': '1', '1': '2'},
        'm2': {'0': '2', '1': '1'},
    }
    classes = [str(i) for i in range(1, 2)]

    ds = create_real_drifting_stream(arff_data['data'], [{'p': 4500, 'w': 10, 'c': ['m1', 'm2']},
                                                         {'p': 9000, 'w': 10, 'c': ['m2', 'm1']}], concepts, 14980)
    arff.write_arff(replace_classes(arff_data['attributes'], classes), ds, 'semi-synth/EEG-SUD', root_dir)
    ds = create_real_drifting_stream(arff_data['data'], [{'p': 4500, 'w': 800, 'c': ['m1', 'm2']},
                                                         {'p': 9000, 'w': 800, 'c': ['m2', 'm1']}], concepts, 14980)
    arff.write_arff(replace_classes(arff_data['attributes'], classes), ds, 'semi-synth/EEG-MED', root_dir)
    ds = create_real_drifting_stream(arff_data['data'], [{'p': 4500, 'w': 3000, 'c': ['m1', 'm2']},
                                                         {'p': 9000, 'w': 3000, 'c': ['m2', 'm1']}], concepts, 14980)
    arff.write_arff(replace_classes(arff_data['attributes'], classes), ds, 'semi-synth/EEG-LNG', root_dir)

def create_static_ratio_streams():
    arff_data = arff.load_arff('real/ACTIVITY/RAW/ACTIVITY_RAW', root_dir, False)
    ds = create_real_drifting_stream(arff_data['data'], [
        {'p': 5000, 'w': 100, 'c': ['m1', 'm1']}], {
            'm1': {'Walking': '1', 'Jogging': '1', 'Upstairs': '2', 'Downstairs': '2', 'Sitting': '1', 'Standing': '3'},
    }, 1048570)
    arff.write_arff(replace_classes(arff_data['attributes'], ['1', '2', '3']), ds, 'imbalanced/static/semi-synth/ACTIVITY_RAW-S1', root_dir)

    arff_data = arff.load_arff('real/CRIMES/CRIMES', root_dir, False)
    ds = create_real_drifting_stream(arff_data['data'], [
        {'p': 300000, 'w': 10000, 'c': ['m1', 'm1']}], {
            'm1': {'WARRANTS': '2', 'OTHER_OFFENSES': '1', 'LARCENY': '1', 'VEHICLE_THEFT': '2', 'VANDALISM': '2',
                   'NON-CRIMINAL': '1', 'ROBBERY': '3', 'ASSAULT': '1', 'WEAPON_LAWS': '4', 'BURGLARY': '3',
                   'SUSPICIOUS_OCC': '3', 'DRUNKENNESS': '4', 'FORGERY': '4', 'DRUG': '2', 'STOLEN_PROPERTY': '4',
                   'SECONDARY_CODES': '4', 'TRESPASS': '4', 'MISSING_PERSON': '3', 'FRAUD': '4', 'KIDNAPPING': '4',
                   'RUNAWAY': '4', 'DRIVING_UNDER_INFLUENCE': '4', 'SEX_OFFENSES_FORCIBLE': '4', 'PROSTITUTION': '4',
                   'DISORDERLY_CONDUCT': '4', 'ARSON': '4', 'FAMILY_OFFENSES': '4', 'LIQUOR_LAWS': '4', 'BRIBERY': '4',
                   'EMBEZZLEMENT': '4', 'SUICIDE': '4', 'LOITERING': '4', 'SEX_OFFENSES_NON_FORCIBLE': '4', 'EXTORTION': '4',
                   'GAMBLING': '4', 'BAD_CHECKS': '4', 'TREA': '4', 'RECOVERED_VEHICLE': '4', 'PORNOGRAPHY': '4'}
    }, 878049)
    arff.write_arff(replace_classes(arff_data['attributes'], ['1', '2', '3', '4']), ds,
                    'imbalanced/static/semi-synth/CRIMES-S1', root_dir)

    arff_data = arff.load_arff('real/DJ30/DJ30', root_dir, False)
    ds = create_real_drifting_stream(arff_data['data'], [
        {'p': 40000, 'w': 1000, 'c': ['m1', 'm1']}], {
            'm1': {'aa': '1', 'axp': '1', 'bs': '1', 'cat': '1', 'ci': '1', 'co': '1', 'dd': '1', 'dis': '1',
                   'ek': '1', 'ge': '1', 'gm': '1', 'hd': '1', 'hon': '1', 'hpq': '1', 'ibm': '1', 'intc': '1',
                   'ip': '1', 'jnj': '1', 'jpm': '1', 'mcd': '1', 'mmm': '2', 'mo': '2', 'mrk': '2', 'msft': '2',
                   'pg': '2', 'sbc': '2', 't': '3', 'utx': '3', 'wmt': '4', 'xom': '4'}
    }, 138166)
    arff.write_arff(replace_classes(arff_data['attributes'], ['1', '2', '3', '4']), ds, 'imbalanced/static/semi-synth/DJ30-S1', root_dir)

    arff_data = arff.load_arff('real/OLYMPIC/OLYMPIC', root_dir, False)
    ds = create_real_drifting_stream(arff_data['data'], [
        {'p': 90000, 'w': 1000, 'c': ['m1', 'm1']}], {
            'm1': {'None': '1', 'Bronze': '2', 'Silver': '2', 'Gold': '3'}
    }, 271116)
    arff.write_arff(replace_classes(arff_data['attributes'], ['1', '2', '3']), ds, 'imbalanced/static/semi-synth/OLYMPIC-S1', root_dir)

    arff_data = arff.load_arff('real/POKER/POKER', root_dir, False)
    ds = create_real_drifting_stream(arff_data['data'], [
        {'p': 300000, 'w': 10000, 'c': ['m1', 'm1']}], {
            'm1': {'0': '1', '1': '1', '2': '2', '3': '2', '4': '3', '5': '3', '6': '4', '7': '4', '8': '3', '9': '4'}
    }, 829201)
    arff.write_arff(replace_classes(arff_data['attributes'], ['1', '2', '3', '4']), ds,
                    'imbalanced/static/semi-synth/POKER-S1', root_dir)

    arff_data = arff.load_arff('real/SENSOR/SENSOR', root_dir, False)
    ds = create_real_drifting_stream(arff_data['data'], [
        {'p': 700000, 'w': 10000, 'c': ['m1', 'm1']}], {
            'm1': {'1': '1', '2': '1', '3': '1', '4': '1', '5': '1', '6': '1', '7': '1', '8': '1', '9': '1', '10': '1',
                   '11': '1', '12': '1', '13': '1', '14': '1', '15': '1', '16': '1', '17': '1', '18': '1', '19': '1', '20': '1',
                   '21': '1', '22': '1', '23': '1', '24': '1', '25': '1', '26': '1', '27': '1', '28': '1', '29': '1', '30': '1',
                   '31': '1', '32': '1', '33': '1', '34': '1', '35': '1', '36': '1', '37': '1', '38': '1', '39': '1', '40': '1',
                   '41': '2', '42': '2', '43': '2', '44': '2', '45': '2', '46': '2', '47': '2', '48': '2', '49': '2', '50': '2',
                   '51': '3', '52': '3', '53': '3', '54': '3', '55': '4', '56': '4', '57': '4', '58': '4'}
    }, 2219802)
    arff.write_arff(replace_classes(arff_data['attributes'], ['1', '2', '3', '4']), ds,
                    'imbalanced/static/semi-synth/SENSOR-S1', root_dir)

    arff_data = arff.load_arff('real/TAGS/TAGS', root_dir, False)
    ds = create_real_drifting_stream(arff_data['data'], [
        {'p': 50000, 'w': 1000, 'c': ['m1', 'm1']}], {
            'm1': {'walking': '1', 'falling': '4', 'lying_down': '3', 'lying': '1', 'sitting_down': '4', 'sitting': '1',
                   'standing_up_from_lying': '2', 'on_all_fours': '3', 'sitting_on_the_ground': '2', 'standing_up_from_sitting': '4',
                   'standing_up_from_sitting_on_the_ground': '4'}
    }, 164860)
    arff.write_arff(replace_classes(arff_data['attributes'], ['1', '2', '3', '4']), ds, 'imbalanced/static/semi-synth/TAGS-S1', root_dir)


def replace_classes(atts, new_classes):
    atts[-1] = ('class', new_classes)
    return atts


def main():
    print("Running...")
    #create_semi_drifting_streams()
    create_semi_drifting_streams()
    #create_semi_imbalanced_drifting_streams()
    #create_synth_drifting_streams()
    #create_static_ratio_streams()


if __name__ == "__main__":
    main()
