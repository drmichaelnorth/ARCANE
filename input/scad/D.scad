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
