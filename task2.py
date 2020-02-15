# -*- coding: utf-8 -*-
__author__ = "Hamahmi"

import argparse

parser = argparse.ArgumentParser(description="NFA")
# in the pdf "0,0;1,2;3,3#0,0;0,1;2,3;3,3#1,2#3"
parser.add_argument("--nfa", type=str, default="0,1;2,1#0,3;3,2#1,0;3,2#2")
parser.add_argument("--str", type=str, default="11")
parser.add_argument("--log", type=bool, default=False)
args = parser.parse_args()


def get_closure_helper(dict_transition, states, states_inc):
    closure = []
    for state in states:
        states_inc.append(state)
        closure += get_closure(dict_transition, state, states_inc)
    return closure


# --if there is a bug just delete staet_inc--
def get_closure(dict_transition, state, states_inc):
    if type(state) == list:
        return get_closure_helper(dict_transition, state, states_inc)
    else:
        if dict_transition.__contains__(state) and not (state in states_inc):
            states_inc.append(state)
            return dict_transition[state] + get_closure(
                dict_transition, dict_transition[state], states_inc
            )
        else:
            return []


def get_all_closures(dict_transition, unique_states):
    closures = dict()
    for state in unique_states:
        closures[state] = sorted(set(get_closure(dict_transition, state, [])))
    return closures


def create_dict(nfa_description):
    nfa = nfa_description.split("#")
    Z = nfa[0].split(";")
    O = nfa[1].split(";")
    E = nfa[2].split(";")
    F = nfa[3]
    accept_states = F.split(",")
    transition_function = dict()
    unique_states = set()
    for state in Z:
        if len(state) > 0:
            transition = state.split(",")
            if transition_function.__contains__(transition[0] + "0"):
                transition_function[transition[0] + "0"] += [transition[1]]
            else:
                transition_function[transition[0] + "0"] = [transition[1]]
            unique_states.add(transition[0])
    for state in O:
        if len(state) > 0:
            transition = state.split(",")
            if transition_function.__contains__(transition[0] + "1"):
                transition_function[transition[0] + "1"] += [transition[1]]
            else:
                transition_function[transition[0] + "1"] = [transition[1]]
            unique_states.add(transition[0])
    for state in E:
        if len(state) > 0:
            transition = state.split(",")
            if transition_function.__contains__(transition[0]):
                transition_function[transition[0]] += [transition[1]]
            else:
                transition_function[transition[0]] = [transition[1]]
            unique_states.add(transition[0])
    return transition_function, accept_states, unique_states


def create_state(states):
    out = ""
    for state in states:
        out += state + ","
    return out[:-1]


def get_transition(states, dict_transition, sym):
    transition = []
    for state in states:
        if dict_transition.__contains__(state + sym):
            transition += dict_transition[state + sym]
    return sorted(set(transition))


def get_closure_state(closure, arr):
    tr = []
    for st in arr:
        tr += [st]
        if st in closure.keys():
            tr += closure[st]
    return sorted(set(tr))


def dfa(nfa_description):
    if args.log:
        print("Converting to DFA")
    dict_transition, accept_states, unique_states = create_dict(nfa_description)
    closure = get_all_closures(dict_transition, unique_states)
    staring_state = ["0"] + closure["0"]

    transition_func = dict()
    remaining = [staring_state]

    while len(remaining) > 0:
        current_state = remaining[0]
        remaining = remaining[1:]

        if transition_func.__contains__(
            create_state(current_state) + ";0"
        ) and transition_func.__contains__(create_state(current_state) + ";1"):
            continue

        zero = get_transition(current_state, dict_transition, "0")
        zero = get_closure_state(closure, zero)
        if zero == []:
            transition_func[create_state(current_state) + ";0"] = "dead"
        else:
            transition_func[create_state(current_state) + ";0"] = create_state(zero)
            remaining += [zero]

        one = get_transition(current_state, dict_transition, "1")
        one = get_closure_state(closure, one)
        if one == []:
            transition_func[create_state(current_state) + ";1"] = "dead"
        else:
            transition_func[create_state(current_state) + ";1"] = create_state(one)
            remaining += [one]

    if "dead" in transition_func.values():
        transition_func["dead;0"] = "dead"
        transition_func["dead;1"] = "dead"

    new_accept_states = []
    for state in transition_func.keys():
        for ac_st in accept_states:
            if ac_st in state.split(";")[0].split(","):
                new_accept_states.append(state.split(";")[0])
                break
    new_accept_states = sorted(set(new_accept_states))
    return transition_func, create_state(staring_state), new_accept_states


def visualize(transition_function, staring_state, accept_states):

    import networkx as nx
    from networkx.drawing.nx_agraph import write_dot
    import os

    G = nx.MultiDiGraph()
    for state in transition_function:
        state_splitted = state.split(";")
        G.add_edge(
            state_splitted[0], transition_function[state], label=state_splitted[1]
        )
    G.add_edge(" ", staring_state)
    G.nodes[" "]["shape"] = "none"
    for node in accept_states:
        G.nodes[node]["style"] = "filled"
    write_dot(G, "graph.dot")
    os.system("dot -Tpng graph.dot > graph.png")
    os.system("graph.png")
    os.system("del graph.dot")


def run(nfa_description, string):
    transition_function, staring_state, accept_states = dfa(nfa_description)
    if args.log:
        import pprint

        pp = pprint.PrettyPrinter(indent=4)
        pp.pprint(transition_function)
        print("Starting State : " + str(staring_state))
        print(
            "Accept State"
            + ("s : " if (len(accept_states) > 0) else " : ")
            + str(accept_states)
        )

        visualize(transition_function, staring_state, accept_states)

    current_state = staring_state
    if args.log:
        print("Running the input string on the generated DFA")
    while len(string) > 0:
        alphabet = string[0]
        if args.log:
            if current_state in accept_states:
                print("(~" + current_state + "~) -" + alphabet + "-> ", end="")
            else:
                print("(" + current_state + ") -" + alphabet + "-> ", end="")
        string = string[1:]
        current_state = transition_function[current_state + ";" + alphabet]
    if args.log:
        if current_state in accept_states:
            print("(~" + current_state + "~)")
        else:
            print("(" + current_state + ")")
    return current_state in accept_states


def check_valid_string(string):
    if args.log:
        print("Checking the validity of the input string")
    lang = "01"
    return all(c in lang for c in string)


def check_valid_nfa(string):
    if args.log:
        print("Checking the validity of the input NFA")
    splitted = string.split("#")
    if len(splitted) != 4:
        return False

    Z = splitted[0].split(";")
    O = splitted[1].split(";")
    E = splitted[2].split(";")
    for z in Z:
        if len(z) > 0 and len(z.split(",")) != 2:
            return False
    for o in O:
        if len(o) > 0 and len(o.split(",")) != 2:
            return False
    for e in E:
        if len(e) > 0 and len(e.split(",")) != 2:
            return False
    return True


if __name__ == "__main__":

    nfa_description = args.nfa
    string_input = args.str

    if check_valid_nfa(nfa_description):
        if args.log:
            print("Valid NFA")
        if check_valid_string(string_input):
            if args.log:
                print("Valid input")
            print(run(nfa_description, string_input))
        else:
            print("The input string must only contain {0, 1}.")
    else:
        print(
            "The nfa desciption must be in the format Z#O#E#F, where Z&O&E are in the form 'i,j,k' seperated with ';', and F is the set of accepted states seperated by ','."
        )
