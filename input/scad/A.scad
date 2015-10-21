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
