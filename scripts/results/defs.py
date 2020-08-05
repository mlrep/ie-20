DRIFT_STREAM_DEFS = {
    "SEA_1": {
        "origin": "SEA",
        "drifts": [
            {"p": 150000, "w": 100, "old": "SEA_concept_2", "new": "SEA_concept_3"},
            {"p": 300000, "w": 100, "old": "SEA_concept_3", "new": "SEA_concept_4"},
            {"p": 450000, "w": 100, "old": "SEA_concept_4", "new": "SEA_concept_3"}
        ],
        "size": 600000,
        "log": 100
    },
    "SEA_2": {
            "origin": "SEA",
            "drifts": [
                {"p": 150000, "w": 10000, "old": "SEA_concept_2", "new": "SEA_concept_3"},
                {"p": 300000, "w": 10000, "old": "SEA_concept_3", "new": "SEA_concept_4"},
                {"p": 450000, "w": 10000, "old": "SEA_concept_4", "new": "SEA_concept_3"}
            ],
            "size": 600000,
            "log": 100
    },
    "STAGGER_1": {
            "origin": "STAGGER",
            "drifts": [
                {"p": 150000, "w": 100, "old": "STAGGER_concept_1", "new": "STAGGER_concept_2"},
                {"p": 300000, "w": 100, "old": "STAGGER_concept_2", "new": "STAGGER_concept_3"},
                {"p": 450000, "w": 100, "old": "STAGGER_concept_3", "new": "STAGGER_concept_1"}
            ],
            "size": 600000,
            "log": 100
    },
    "STAGGER_2": {
            "origin": "STAGGER",
            "drifts": [
                {"p": 150000, "w": 10000, "old": "STAGGER_concept_1", "new": "STAGGER_concept_2"},
                {"p": 300000, "w": 10000, "old": "STAGGER_concept_2", "new": "STAGGER_concept_3"},
                {"p": 450000, "w": 10000, "old": "STAGGER_concept_3", "new": "STAGGER_concept_1"}
            ],
            "size": 600000,
            "log": 100
    },
    "RBF_1": {
            "origin": "RBF",
            "drifts": [
                {"p": 250000, "w": 100, "old": "RBF_concept_1_n5", "new": "RBF_concept_2_n5"},
                {"p": 500000, "w": 100, "old": "RBF_concept_2_n5", "new": "RBF_concept_4_n5"},
                {"p": 750000, "w": 100, "old": "RBF_concept_4_n5", "new": "RBF_concept_5_n5"}
            ],
            "size": 1000000,
            "log": 100
    },
    "RBF_2": {
            "origin": "RBF",
            "drifts": [
                {"p": 250000, "w": 10000, "old": "RBF_concept_1_n5", "new": "RBF_concept_2_n5"},
                {"p": 500000, "w": 10000, "old": "RBF_concept_2_n5", "new": "RBF_concept_4_n5"},
                {"p": 750000, "w": 10000, "old": "RBF_concept_4_n5", "new": "RBF_concept_5_n5"}
            ],
            "size": 1000000,
            "log": 100
    },
    "RBF_3": {
            "origin": "RBF",
            "drifts": [
                {"p": 400000, "w": 50000, "old": "RBF_concept_11_n15_12m", "new": "RBF_concept_12_n15_12m"},
                {"p": 800000, "w": 50000, "old": "RBF_concept_12_n15_12m", "new": "RBF_concept_13_n15_12m"},
            ],
            "size": 1200000,
            "log": 100
    },
    "RBF_4": {
        "origin": "RBF",
        "drifts": [
            {"p": 400000, "w": 100000, "old": "RBF_concept_11_n15_12m", "new": "RBF_concept_12_n15_12m"},
            {"p": 800000, "w": 100000, "old": "RBF_concept_12_n15_12m", "new": "RBF_concept_13_n15_12m"},
        ],
        "size": 1200000,
        "log": 100
    },
    "TREE_1": {
        "origin": "TREE",
        "drifts": [
            {"p": 250000, "w": 100, "old": "TREE_concept_2", "new": "TREE_concept_1"},
            {"p": 500000, "w": 100, "old": "TREE_concept_1", "new": "TREE_concept_3"},
            {"p": 750000, "w": 100, "old": "TREE_concept_3", "new": "TREE_concept_4"}
        ],
        "size": 1000000,
        "log": 100
    },
    "TREE_2": {
        "origin": "TREE",
        "drifts": [
            {"p": 250000, "w": 10000, "old": "TREE_concept_2", "new": "TREE_concept_1"},
            {"p": 500000, "w": 10000, "old": "TREE_concept_1", "new": "TREE_concept_3"},
            {"p": 750000, "w": 10000, "old": "TREE_concept_3", "new": "TREE_concept_4"}
        ],
        "size": 1000000,
        "log": 100
    },
    "TREE_3": {
        "origin": "TREE",
        "drifts": [
            {"p": 400000, "w": 50000, "old": "TREE_concept_2_12m", "new": "TREE_concept_1_12m"},
            {"p": 800000, "w": 50000, "old": "TREE_concept_1_12m", "new": "TREE_concept_3_12m"},
        ],
        "size": 1200000,
        "log": 100
    },
    "TREE_4": {
        "origin": "TREE",
        "drifts": [
            {"p": 400000, "w": 100000, "old": "TREE_concept_2_12m", "new": "TREE_concept_1_12m"},
            {"p": 800000, "w": 100000, "old": "TREE_concept_1_12m", "new": "TREE_concept_3_12m"},
        ],
        "size": 1200000,
        "log": 100
    },
    "HYPERPLANE_1": {
        "origin": "HYPERPLANE",
        "drifts": None,
        "size": 500000,
        "log": 100
    },
    "HYPERPLANE_2": {
        "origin": "HYPERPLANE",
        "drifts": None,
        "size": 500000,
        "log": 100
    }
}