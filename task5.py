# -*- coding: utf-8 -*-
# T16_37_15881_Abdelrahman_Gharib_ElHamahmi

__author__ = "Hamahmi"


def immediate(rule):
    splitted = rule.split(",")
    A = splitted[0]
    Ap = A + "'"
    # Ap = A + "′" # cooler
    alphas = []
    betas = []
    for sentential in splitted[1:]:
        if sentential[0] == A:
            alphas.append(sentential[1:])
        else:
            betas.append(sentential)
    if len(alphas) == 0:
        return rule
    output = A + ","
    for beta in betas:
        output += beta + Ap + ","
    output = output[:-1] + ";" + Ap + ","
    for alpha in alphas:
        output += alpha + Ap + ","
    return output


def LRE(cfg_lre):
    cfg_lre = cfg_lre.replace(" ", "")
    rules = cfg_lre.split(";")
    for i in range(len(rules)):
        for j in range(i):
            Ai = rules[i].split(",")
            Aj = rules[j].split(";")[0].split(",")
            new_Ai = Ai[0] + ","
            for x in range(1, len(Ai)):
                if Ai[x][0] == Aj[0]:
                    y = Ai[x][1:]
                    for k in range(1, len(Aj)):
                        new_Ai += Aj[k] + y + ","
                else:
                    new_Ai += Ai[x] + ","
            rules[i] = new_Ai[:-1]
        rules[i] = immediate(rules[i])
    cfg = ""
    for i in range(len(rules)):
        cfg += rules[i] + ";"
    return cfg[:-1]


if __name__ == "__main__":
    input = "S, ScT, T; T, aSb, iaLb, i; L, SdL, S"
    output = LRE(input)
    print(output)
