package com.bus.chelaile.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.bus.chelaile.model.rule.Position;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

public class LocationKDTree {
    private static final int K = 3; // 3-d tree
    private final Node tree;
    private static final double latPer1Km = 0.0091;
    private static final double lngPer1Km = 0.0111;

    public LocationKDTree(final List<Position> positions) {
        final List<Node> nodes = new ArrayList<>(positions.size());
        for (final Position position : positions) {
            nodes.add(new Node(position));
        }
        tree = buildTree(nodes, 0);
    }

    public Position findNearest(final double latitude, final double longitude) {
        Node node = findNearest(tree, new Node(longitude, latitude), 0);
        /*System.out.println(GpsUtils.distance(node.position.getLng(), node.position.getLat(),
                longitude, latitude));*/
        //int r = node.position.getDist();
        /*if (node == null) {
        	return null;
        }
        if (Math.abs(latitude - node.position.getLat()) > node.position.getDist() / 1000.0 * latPer1Km) {
        	return null;
        }
        if (Math.abs(longitude - node.position.getLng()) > node.position.getDist() / 1000.0 * lngPer1Km) {
        	return null;
        }*/
        if (GpsUtils.distance(node.position.getLng(), node.position.getLat(), longitude, latitude) * 1000 > node.position
                .getDist()) {
            return null;
        }
        return node.position;
    }

    private static Node findNearest(final Node current, final Node target, final int depth) {
        final int axis = depth % K;
        final int direction = getComparator(axis).compare(target, current);
        final Node next = (direction < 0) ? current.left : current.right;
        final Node other = (direction < 0) ? current.right : current.left;
        Node best = (next == null) ? current : findNearest(next, target, depth + 1);
        if (current.euclideanDistance(target) < best.euclideanDistance(target)) {
            best = current;
        }
        if (other != null) {
            if (current.verticalDistance(target, axis) < best.euclideanDistance(target)) {
                final Node possibleBest = findNearest(other, target, depth + 1);
                if (possibleBest != null && (possibleBest.euclideanDistance(target) < best.euclideanDistance(target))) {
                    best = possibleBest;
                }
            }
        }
        /*System.out.println(best.position.getLng());
        System.out.println(best.position.getLat());
        System.out.println(target.position.getLng());
        System.out.println(target.position.getLat());
        System.out.println(GpsUtils.distance(best.position.getLng(), best.position.getLat(),
                target.position.getLng(), target.position.getLat()));
        System.out.println("--------------------------------------");*/
        return best;
    }

    private static Node buildTree(final List<Node> items, final int depth) {
        if (items.isEmpty()) {
            return null;
        }

        Collections.sort(items, getComparator(depth % K));
        final int index = items.size() / 2;
        final Node root = items.get(index);
        root.left = buildTree(items.subList(0, index), depth + 1);
        root.right = buildTree(items.subList(index + 1, items.size()), depth + 1);
        return root;
    }

    private static class Node {
        Node left;
        Node right;
        Position position;
        final double[] point = new double[K];

        Node(final double longitude, final double latitude) {
            this.position = new Position(longitude, latitude);
            point[0] = (double) (cos(toRadians(latitude)) * cos(toRadians(longitude)));
            point[1] = (double) (cos(toRadians(latitude)) * sin(toRadians(longitude)));
            point[2] = (double) (sin(toRadians(latitude)));
        }

        Node(final Position position) {
            this(position.getLng(), position.getLat());
            this.position = position;
        }

        double euclideanDistance(final Node that) {
            final double x = this.point[0] - that.point[0];
            final double y = this.point[1] - that.point[1];
            final double z = this.point[2] - that.point[2];
            return x * x + y * y + z * z;
        }

        double verticalDistance(final Node that, final int axis) {
            final double d = this.point[axis] - that.point[axis];
            return d * d;
        }
    }

    private static Comparator<Node> getComparator(final int i) {
        return NodeComparator.values()[i];
    }

    private static enum NodeComparator implements Comparator<Node> {
        x {
            @Override
            public int compare(final Node a, final Node b) {
                return Double.compare(a.point[0], b.point[0]);
            }
        },
        y {
            @Override
            public int compare(final Node a, final Node b) {
                return Double.compare(a.point[1], b.point[1]);
            }
        },
        z {
            @Override
            public int compare(final Node a, final Node b) {
                return Double.compare(a.point[2], b.point[2]);
            }
        }
    }

    private static void printPosition(Position pos) {
        if (pos == null) {
            System.out.println("location = NULL");
        } else {
            System.out.println(pos.getName());
        }
    }

    public static void main(String[] args) {
        List<Position> positions = new ArrayList<>();
        positions.add(new Position(116.3688182616, 39.9927821321, 3000, "广泰超市总店"));
        positions.add(new Position(113.174506748, 23.0887020736, 2000, "广泰超市"));
        positions.add(new Position(113.171411979, 23.1000601652, 2000, "广泰超市华夏店"));
        positions.add(new Position(113.152119749, 23.1117749321, 2000, "广泰超市"));
        positions.add(new Position(113.184750432, 23.1121938454, 2000, "广泰超市"));
        positions.add(new Position(113.113249733, 23.1162737335, 2000, "广泰超市"));
        positions.add(new Position(113.119928499, 23.1060661705, 2000, "广泰超市"));
        positions.add(new Position(113.119546733, 23.1187747335, 2000, "广泰超市"));
        positions.add(new Position(113.097227591, 23.1470210451, 2000, "广泰超市"));
        positions.add(new Position(113.098686346, 23.1340683809, 2000, "广泰超市"));
        positions.add(new Position(113.07683558, 23.2288609019, 2000, "广泰超市"));
        positions.add(new Position(113.070912939, 23.2257844887, 2000, "广泰超市"));
        positions.add(new Position(113.045375897, 23.0613857814, 2000, "广泰超市"));
        positions.add(new Position(113.049930337, 23.0600989152, 2000, "广泰超市"));
        positions.add(new Position(113.013725678, 23.0645208248, 2000, "广泰超市"));
        positions.add(new Position(112.975132818, 23.0946050073, 2000, "广泰超市"));
        positions.add(new Position(112.917219637, 23.1107706346, 2000, "广泰超市"));
        positions.add(new Position(112.910362611, 23.0565104697, 2000, "广泰超市"));
        positions.add(new Position(112.917219637, 23.1107706346, 2000, "广泰超市"));
        positions.add(new Position(112.899020121, 23.1013259, 2000, "广泰超市"));
        positions.add(new Position(112.91523798, 23.0428481971, 2000, "广泰超市"));
        positions.add(new Position(116.4037276101, 39.9941325884, 1000, "远大中心B座"));
        positions.add(new Position(116.3688182616, 39.9927821321, 3000, "志祯测试"));

        LocationKDTree kdTree = new LocationKDTree(positions);
        // Position loc = new Position(23.10132, 112.8990201, -1);
        double beginMillisecond = System.currentTimeMillis();
        Position loc = kdTree.findNearest(39.994914, 116.4037);
        //        printPosition(loc);
        //        System.out.println("===========================================");
        //        loc = kdTree.findNearest(39.0495835236,117.2237307424);
        //        printPosition(loc);
        //        System.out.println("===========================================");
        //        loc = kdTree.findNearest(23.0970645236,113.1844696512);
        //        printPosition(loc);
        //        System.out.println("===========================================");
        //        loc = kdTree.findNearest(23.0986986551,113.1737082920);
        //        printPosition(loc);
        //        System.out.println("===========================================");
        //        loc = kdTree.findNearest(23.1056711583,113.1693825221);
        //        printPosition(loc);
        //        System.out.println("===========================================");
        //        loc = kdTree.findNearest(39.9948585884,116.4069256101);
        //        printPosition(loc);
        //        System.out.println("===========================================");
        //        loc = kdTree.findNearest(39.9070239520,116.3913756741);
        //        printPosition(loc);
        //        System.out.println("===========================================");
        //        loc = kdTree.findNearest(39.994914,116.4037);
        //        printPosition(loc);
        //        System.out.println("===========================================");
        System.out.println(String.format("Find used %f milliseconds", System.currentTimeMillis() - beginMillisecond));
    }
}
