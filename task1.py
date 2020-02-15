# -*- coding: utf-8 -*-
__author__ = "Hamahmi"

import argparse


def dfa(dfa_description):
    all = dfa_description.split("#")
    states = all[0].split(";")
    accept_states = all[-1].split(",")
    transition_function = dict()
    for state in states:
        transition = state.split(",")
        transition_function[transition[0] + "0"] = transition[1]
        transition_function[transition[0] + "1"] = transition[2]
    return transition_function, accept_states


def run(dfa_description, string):
    transition_function, accept_states = dfa(dfa_description)
    current_state = "0"
    while len(string) > 0:
        alphabet = string[0]
        string = string[1:]
        current_state = transition_function[current_state + alphabet]
    return current_state in accept_states


def check_valid_string(string):
    lang = "01"
    return all(c in lang for c in string)


def check_valid_dfa(string):
    if not ("#" in string):
        return False

    splitted = string.split("#")
    if len(splitted) != 2:
        return False

    states = splitted[0].split(";")
    for state in states:
        if len(state.split(",")) != 3:
            return False
    return True


if __name__ == "__main__":

    parser = argparse.ArgumentParser(description="DFA")
    parser.add_argument("--dfa", type=str, default="0,0,1;1,2,1;2,0,3;3,3,3#1,3")
    parser.add_argument("--str", type=str, default="0100")
    args = parser.parse_args()

    dfa_description = args.dfa
    string_input = args.str

    if check_valid_dfa(dfa_description):
        if check_valid_string(string_input):
            print(run(dfa_description, string_input))
        else:
            print("The input string must only contains {0, 1}.")
    else:
        print(
            "The dfa desciption must be in the format P#S, where P is i,j,k seperated with ;, and S is the set of accepted states seperated by ,."
        )
