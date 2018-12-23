package com.sakaimobile.development.sakaiclient20;

import com.sakaimobile.development.sakaiclient20.persistence.entities.Resource;

import java.util.ArrayList;
import java.util.List;

public class Node {

    public Resource resource;

    public List<Node> children;

    public Node(Resource r) {
        this.resource = r;
        this.children  = null;
    }

}
