#!/usr/bin/env python
# By Isaac Matthews
import sys, argparse
from graphviz import Source
import pydot
import os
import re
from collections import defaultdict

def cli_opt():
    prsr = argparse.ArgumentParser(
    formatter_class=argparse.RawDescriptionHelpFormatter,
    description='Process Dot files',
    usage='%(prog)s file.dot [OPTIONS]',
    )

    prsr.add_argument('file', action='store', help='Dot File')
    prsr.add_argument('-minscore', action='store', dest='minscore', nargs='?', default='not_set', help='Min score to process')

    args = prsr.parse_args()

    return args

def filter_rules(graph):
    rule_exp = re.compile('.*:RULE.*')
    edge_list = graph.get_edges()
    for node in graph.get_nodes():
        #print(node.get_attributes()['label'])
        m = rule_exp.match(node.get_attributes()['label'])
        starts=[]
        ends=[]
        if m :
            name = node.get_name()
            for edge in edge_list:
                s = edge.to_string().split(' -> ')
                s[1]=s[1].replace(';','')
                if name == s[0]:
                    ends.append(s[1])
                    #print('added end ' + s[1])
                if name == s[1]:
                    starts.append(s[0])
                    #print('added start ' + s[0])
                if name == s[0] or name == s[1]:
                    graph.del_edge(s[0],s[1])
                        #print('deleted ' + s[0] + ' to ' +s[1])
            graph.del_node(name)
            for start in starts:
                for end in ends:
                    ed = pydot.Edge(start,end)
                    #print('edge '+ ed.to_string())
                    graph.add_edge(ed)
            #print('match' + node.get_name())
            #print(edge_list[int(node.get_name())])
    return graph

def edge_dictionary(graph):
    dic = defaultdict(list)
    edge_list = graph.get_edges()
    for edge in edge_list:
        s = edge.to_string().split(' -> ')
        s[1]=s[1].replace(';','')
        #print("first is" + s[0] + s[1])
    return dic


def main () :
    args = cli_opt()

    if args.minscore != "not_set":
        print("set min")
    #file = open(args.file, 'r')
    #text=file.read()
    graph = pydot.graph_from_dot_file(args.file)[0]
    #for edge in graph.get_edge('6','777'):
    #    print(edge.to_string())
    #print(graph.get_type())
    edge_dictionary(graph)
    graph = filter_rules(graph)
    processor = pydot.Dot(graph)
    processor.write(args.file.split('.')[0]+'-processed.dot')
    #print(graph.get_node('1547'))

if __name__ == "__main__":
    main()
