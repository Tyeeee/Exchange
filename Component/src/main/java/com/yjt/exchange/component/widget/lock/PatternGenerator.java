package com.hynet.heebit.components.widget.lock;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Random;

public class PatternGenerator {

    protected int mLength;
    protected int mMinNodes;
    protected int mMaxNodes;
    protected Random mRng;
    protected List<Point> mNodes;

    public PatternGenerator() {
        mRng = new Random();
        setLength(0);
        setMinNodes(0);
        setMaxNodes(0);
    }

    public List<Point> getPattern() {
        List<Point> pattern = Lists.newArrayList();
        if (mMaxNodes < 1) {
            return pattern;
        }
        // list for random access, set for fast membership testing
        List<Point> nodeAvailList = Lists.newArrayList(mNodes);
        int pathMaxLen = (int) Math.min(mMaxNodes, Math.pow(mLength, 2));
        int pathLen = mRng.nextInt(pathMaxLen - mMinNodes + 1) + mMinNodes;

        Point tail = nodeAvailList.remove(mRng.nextInt(nodeAvailList.size()));
        pattern.add(tail);
        for (int ii = 1; ii < pathLen; ii++) {
            // build list of possible points
            List<Point> nodeCandidatesList = Lists.newArrayList(nodeAvailList);
            for (int i = 0; i < nodeCandidatesList.size(); i++) {
                Point delta = new Point(nodeCandidatesList.get(i).x - tail.x,
                                        nodeCandidatesList.get(i).y - tail.y);
                // remove this point if there is other unused point between this
                // one and "tail"
                int gcd = Math.abs(computeGcd(delta.x, delta.y));
                if (gcd > 1) {
                    for (int j = 1; j < gcd; j++) {
                        Point inside = new Point(tail.x + delta.x / gcd * j,
                                                 tail.y + delta.y / gcd * j);
                        if (nodeAvailList.contains(inside)) {
                            nodeCandidatesList.remove(i);
                            i--;
                            break;
                        }
                    }
                }
            }
            Point next = nodeCandidatesList.get(mRng.nextInt(nodeCandidatesList
                                                                     .size()));
            // remove from consideration and add to pattern
            nodeAvailList.remove(next);
            pattern.add(next);
            tail = next;
        }
        return pattern;
    }

    public void setLength(int length) {
        // build the prototype set to copy from later
        List<Point> nodes = Lists.newArrayList();
        for (int y = 0; y < length; y++) {
            for (int x = 0; x < length; x++) {
                nodes.add(new Point(x, y));
            }
        }
        mNodes = nodes;
        mLength = length;
    }

    public int getLength() {
        return mLength;
    }

    public void setMinNodes(int nodes) {
        mMinNodes = nodes;
    }

    public int getMinNodes() {
        return mMinNodes;
    }

    public void setMaxNodes(int nodes) {
        mMaxNodes = nodes;
    }

    public int getMaxNodes() {
        return mMaxNodes;
    }

    public static int computeGcd(int a, int b) {
        if (b > a) {
            int temp = a;
            a = b;
            b = temp;
        }
        while (b != 0) {
            int m = a % b;
            a = b;
            b = m;
        }
        return a;
    }
}
