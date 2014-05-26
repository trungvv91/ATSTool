/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.textprocess;

import java.util.ArrayList;

/**
 *
 * @author Trung
 */
class GraphNode {

    ArrayList<MyToken> list;
    ArrayList<GraphNode> nextNodes;
//    GraphNode prevNode;

    public GraphNode() {
        list = new ArrayList<>();
        nextNodes = new ArrayList<>();
    }

    public static GraphNode CreateStartNode() {
        GraphNode start = new GraphNode();
        MyToken st = new MyToken("<START>", "<START>", "<START>");
        st.tf_isf = 0;
        start.list.add(st);
        return start;
    }

//    public static GraphNode CreateEndNode() {
//        GraphNode end = new GraphNode();
//        MyToken et = new MyToken("<END>", "<END>", "<END>");
//        et.tf_isf = 0;
//        end.list.add(et);
//        return end;
//    }
}

public class WordGraphs {

    ArrayList<GraphNode> nodes;
    ArrayList<MySentence> candidates;
    final int M = 3;

    public WordGraphs(ArrayList<MySentence> sentences) {
        nodes = new ArrayList<>();
        GraphNode start = GraphNode.CreateStartNode();
        nodes.add(start);
        for (MySentence sentence : sentences) {
            GraphNode prev = start;
            for (MyToken token : sentence.tokensList) {
                GraphNode newNode = null;
                for (GraphNode node : nodes) {
                    MyToken gtoken = node.list.get(0);
                    if (token.equals(gtoken)) {
                        newNode = node;
                        break;
                    }
                }
                if (newNode == null) {
                    newNode = new GraphNode();
                    nodes.add(newNode);
                }
                newNode.list.add(token);
                if (!prev.nextNodes.contains(newNode)) {
                    prev.nextNodes.add(newNode);
                }
                prev = newNode;
            }
        }
    }

    void findPath(ArrayList<MyToken> path, GraphNode node, int pri) {
        if (node.list.get(pri).endOfSentence) {
            MySentence newPath = new MySentence();
            for (MyToken token : path) {
                newPath.tokensList.add(token);
            }
            newPath.tokensList.add(node.list.get(pri));
            candidates.add(newPath);
        } else {
            MyToken token = node.list.get(pri);
            int index = path.size();
            path.add(index, token);
            for (GraphNode next : node.nextNodes) {
                for (int i = 0; i < next.list.size(); i++) {
                    MyToken nextToken = next.list.get(i);
                    if (nextToken.nSentence == token.nSentence && nextToken.nPosition > token.nPosition
                            && nextToken.nPosition < token.nPosition + M) {
//                        if (token.word.equals("đặc_biệt")) {
//                            System.out.println("df");
//                        }
                        findPath(path, next, i);
                    }
                }
            }
            path.remove(index);
        }
    }

    public ArrayList<MySentence> generateSentences() {
        candidates = new ArrayList<>();
        GraphNode start = nodes.get(0);
        ArrayList<MyToken> s = new ArrayList<>();
        for (GraphNode node : start.nextNodes) {
            for (int i = 0; i < node.list.size(); i++) {
                MyToken token = node.list.get(i);
                if (token.nPosition < M) {
                    findPath(s, node, i);
                }
            }
        }
        return candidates;
    }

    public static void main(String[] args) {
        MyTokenizer tokenizer = new MyTokenizer();
        ArrayList<MySentence> sentences = tokenizer.createTokens("corpus/Plaintext/kinhte/KT01.txt");
        MyReducer re = new MyReducer(sentences);
        WordGraphs wg = new WordGraphs(re.reduction());
        sentences = wg.generateSentences();
        for (MySentence mySentence : sentences) {
            System.out.println(mySentence.toString());
            System.out.println(mySentence.getScore());
        }
    }
}
