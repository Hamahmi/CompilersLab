# -*- coding: utf-8 -*-
__author__ = "Hamahmi"

import argparse

parser = argparse.ArgumentParser(description="Fallback DFA")
parser.add_argument(
    "--fdfa", type=str, default="0,0,1,00;1,2,1,01;2,0,3,10;3,3,3,11#0,1,2"
)
parser.add_argument("--str", type=str, default="1011100")
parser.add_argument("--log", dest="log", action="store_true")
args = parser.parse_args()


def fdfa(fdfa_description):
    fdfa_splitted = fdfa_description.split("#")
    states = fdfa_splitted[0].split(";")
    accept_states = fdfa_splitted[-1].split(",")
    transition_function = dict()
    actions = dict()
    for state in states:
        transition = state.split(",")
        transition_function[transition[0] + ";0"] = transition[1]
        transition_function[transition[0] + ";1"] = transition[2]
        actions[transition[0]] = transition[3]
    return transition_function, accept_states, actions


def get_state_with_action(state, actions):
    return state + "\n{" + actions[state] + "}"


def visualize(transition_function, accept_states, actions):

    import networkx as nx
    from networkx.drawing.nx_agraph import write_dot
    import os

    G = nx.MultiDiGraph()
    for state in transition_function:
        state_splitted = state.split(";")
        G.add_edge(
            get_state_with_action(state_splitted[0], actions),
            get_state_with_action(transition_function[state], actions),
            label=state_splitted[1],
        )
    G.add_edge(" ", get_state_with_action("0", actions))
    G.nodes[" "]["shape"] = "none"
    for node in accept_states:
        G.nodes[get_state_with_action(node, actions)]["style"] = "filled"
    write_dot(G, "graph.dot")
    os.system("dot -Tpng graph.dot > graph.png")
    os.system("graph.png")
    os.system("del graph.dot")


def run(fdfa_description, input_string):
    transition_function, accept_states, actions = fdfa(fdfa_description)

    if args.log:
        import pprint

        pp = pprint.PrettyPrinter(indent=5)
        pp.pprint(transition_function)
        print("Actions : ")
        pp.pprint(actions)
        print(
            "Accept State"
            + ("s : " if (len(accept_states) > 0) else " : ")
            + str(accept_states)
        )
        visualize(transition_function, accept_states, actions)
        print("Running the input string on the generated FDFA")

    stack = []
    L = 0
    R = 0

    output = ""

    while R < len(string_input):
        if args.log:
            print("String : " + string_input[R:])
        current_state = "0"
        stack.append(current_state)
        while L < len(string_input):
            alphabet = string_input[L]
            L += 1
            if args.log:
                print(current_state + " -- " + alphabet + " --> ", end="")
            current_state = transition_function[current_state + ";" + alphabet]
            stack.append(current_state)
        if args.log:
            print(current_state)
        if stack[-1] in accept_states:
            output += actions[stack[-1]]
            break

        else:
            ES = True
            qr = stack[-1]
            while len(stack) > 0:
                qa = stack[-1]
                stack = stack[:-1]
                L -= 1
                if qa in accept_states:
                    output += actions[qa]
                    L += 1
                    R = L
                    stack = []
                    ES = False
            if ES:
                output += actions[qr]
                break
    return output


def check_valid_string(string):
    if args.log:
        print("Checking the validity of the input string")
    lang = "01"
    return all(c in lang for c in string)


def check_valid_fdfa(string):
    if args.log:
        print("Checking the validity of the input NFA")
    splitted = string.split("#")
    if len(splitted) != 2:
        return False

    P = splitted[0].split(";")
    for p in P:
        if len(p) > 0 and len(p.split(",")) != 4:
            return False
    return True


if __name__ == "__main__":

    fdfa_description = args.fdfa
    string_input = args.str

    if check_valid_fdfa(fdfa_description):
        if args.log:
            print("Valid FDFA")
        if check_valid_string(string_input):
            if args.log:
                print("Valid input")
            print(run(fdfa_description, string_input))
        else:
            print("The input string must only contain {0, 1}.")
    else:
        print(
            "The fdfa desciption must be in the format P#S, where P is in the form 'i,j,k,s' seperated with ';', and F is the set of accepted states seperated by ','."
        )
