package com.sinosoft.xreport.dl;

/**
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author houzw
 * @version 1.0
 */
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Vector;

import javax.swing.tree.TreeNode;

public class TreeNodeImpl implements TreeNode
{

    protected Vector children = new Vector();

    /** this node's parent, or null if this node has no parent */
    protected TreeNode parent;

    /** true if the node is able to have children */
    protected boolean allowsChildren;

    public TreeNodeImpl()
    {
    }


    /**
     * Returns the child <code>TreeNode</code> at index
     * <code>childIndex</code>.
     */
    public TreeNode getChildAt(int childIndex)
    {
        if (children == null)
        {
            throw new ArrayIndexOutOfBoundsException("node has no children");
        }
        return (TreeNode) children.elementAt(childIndex);
    }

    /**
     * Returns the number of children <code>TreeNode</code>s the receiver
     * contains.
     */
    public int getChildCount()
    {
        if (children == null)
        {
            return 0;
        }
        else
        {
            return children.size();
        }
    }

    /**
     * Returns the parent <code>TreeNode</code> of the receiver.
     */
    public TreeNode getParent()
    {
        return parent;
    }

    /**
     * Returns the index of <code>node</code> in the receivers children.
     * If the receiver does not contain <code>node</code>, -1 will be
     * returned.
     */
    public int getIndex(TreeNode node)
    {
        if (node == null)
        {
            throw new IllegalArgumentException("argument is null");
        }

        if (!isNodeChild(node))
        {
            return -1;
        }
        return children.indexOf(node); // linear search

    }


    /**
     * Returns true if <code>aNode</code> is a child of this node.  If
     * <code>aNode</code> is null, this method returns false.
     *
     * @return	true if <code>aNode</code> is a child of this node; false if
     *  		<code>aNode</code> is null
     */
    public boolean isNodeChild(TreeNode aNode)
    {
        boolean retval;

        if (aNode == null)
        {
            retval = false;
        }
        else
        {
            if (getChildCount() == 0)
            {
                retval = false;
            }
            else
            {
                retval = (aNode.getParent() == this);
            }
        }
        return retval;
    }

    /**
     * Returns true if the receiver allows children.
     */
    public boolean getAllowsChildren()
    {
        return allowsChildren;
    }

    /**
     * Returns true if the receiver is a leaf.
     */
    public boolean isLeaf()
    {
        return (getChildCount() == 0);
    }

    /**
     * Returns the children of the reciever as an Enumeration.
     */
    public Enumeration children()
    {
        if (children == null)
        {
            return EMPTY_ENUMERATION;
        }
        else
        {
            return children.elements();
        }
    }

    /**
     * An enumeration that is always empty. This is used when an enumeration
     * of a leaf node's children is requested.
     */
    static public final Enumeration EMPTY_ENUMERATION
            = new Enumeration()
    {
        public boolean hasMoreElements()
        {
            return false;
        }

        public Object nextElement()
        {
            throw new NoSuchElementException("No more elements");
        }
    };
}