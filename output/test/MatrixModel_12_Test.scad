// Define the node renderer.
//  Parameters:
//  n - the index in the nodes table of the node to render
module renderNode(n) {
    translate(nodeCoordinates[n]) sphere(r = 5, center = true);
};

// Define the edge renderer.
//  Parameters:
//  src - the index in the nodes table of the source node
//  dst - the index in the nodes table of the destination node
module renderEdge(src, dst) {

    // Find the source and destination node coordinates.
    srcNode  = nodeCoordinates[src];
    destNode = nodeCoordinates[dst];

    // Find the vector connecting the source and destination node coordinates.
	diff = [destNode[0] - srcNode[0], destNode[1] - srcNode[1], destNode[2] - srcNode[2]];

    // Find the distance between the source and destination node coordinates.
	length = sqrt(pow(diff[0], 2) + pow(diff[1], 2) + pow(diff[2], 2));

    // Move to the starting point.
	translate(diff / 2 + srcNode) {

        // Rotate around the Z axis.
        rotate([0, 0, atan2(diff[1], diff[0])]) {

            // Rotate around the Y axis.
            rotate([0, atan2(sqrt(pow(diff[0], 2) + pow(diff[1], 2)), diff[2]), 0]) {

                // Draw the edge.
                cylinder(h = length, r = 1.0, center = true);

            }

        }

    }

};

// Define the list of node coordinates.
nodeCoordinates = [
[665.0, 350.0, 115.55123658929773], 
[447.3403532281084, 649.5828026329734, 458.12608176178617], 
[95.1596467718916, 535.1523544721291, 244.42013822996074], 
[95.15964677189154, 164.847645527871, 320.6684697845816], 
[447.34035322810837, 50.417197367026574, 421.8797364660057]
];

// Define the list of node identifiers.
nodeIdentifiers = [
[0, 0], 
[1, 1], 
[2, 2], 
[3, 3], 
[4, 4]
];

// Define the list of edges.
edgeIdentifiers = [
[1, 0, 0], 
[2, 0, 1], 
[3, 1, 0], 
[4, 1, 2], 
[5, 1, 3], 
[6, 1, 4], 
[7, 2, 0], 
[8, 3, 0], 
[9, 4, 1], 
[10, 4, 2], 
[11, 4, 3], 
[12, 0, 0], 
[13, 0, 1], 
[14, 1, 0], 
[15, 1, 2], 
[16, 1, 3], 
[17, 1, 4], 
[18, 2, 0], 
[19, 3, 1], 
[20, 3, 3], 
[21, 4, 0], 
[22, 4, 2], 
[23, 1, 2], 
[24, 1, 3], 
[25, 2, 1], 
[26, 2, 4], 
[27, 3, 1], 
[28, 3, 2], 
[29, 3, 3], 
[30, 3, 4], 
[31, 4, 1], 
[32, 4, 4]
];

// Render the network nodes.
for (nodeIndex = [0 : len(nodeCoordinates) - 1] ) {
renderNode(nodeIndex);
};

// Render the network edges.
for (edgeIndex = [0 : len(edgeIdentifiers) - 1] ) {
renderEdge(
    lookup(edgeIdentifiers[edgeIndex][1], nodeIdentifiers),
    lookup(edgeIdentifiers[edgeIndex][2], nodeIdentifiers)
);
};
