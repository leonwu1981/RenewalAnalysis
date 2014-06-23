/**
 * 作者：王育春
 * 日期：2006-3-13
 * 邮件：wangyc@sinosoft.com.cn
 */
package com.sinosoft.utility.treetable;

public class TreeTableRow {
	private TreeTable treeTable;

	private Object[] Values;

	private String NodeCode;

	private String NodeName;

	private String ParentNodeCode;

	private String ChildFlag;

	private String NodeOrder;

	private String SelectFlag;

	private String Level;

	private int ColCount;

	public String getChildFlag() {
		if (NodeCode == null) {
			init();
		}
		return ChildFlag;
	}

	public void setChildFlag(String childFlag) {
		ChildFlag = childFlag;
	}

	public String getLevel() {
		if (NodeCode == null) {
			init();
		}
		return Level;
	}

	public void setLevel(String level) {
		Level = level;
	}

	public String getNodeCode() {
		if (NodeCode == null) {
			init();
		}
		return NodeCode;
	}

	public void setNodeCode(String nodeCode) {
		NodeCode = nodeCode;
	}

	public String getNodeName() {
		if (NodeCode == null) {
			init();
		}
		return NodeName;
	}

	public void setNodeName(String nodeName) {
		NodeName = nodeName;
	}

	public String getNodeOrder() {
		if (NodeCode == null) {
			init();
		}
		return NodeOrder;
	}

	public void setNodeOrder(String nodeOrder) {
		NodeOrder = nodeOrder;
	}

	public String getParentNodeCode() {
		if (NodeCode == null) {
			init();
		}
		return ParentNodeCode;
	}

	public void setParentNodeCode(String parentNodeCode) {
		ParentNodeCode = parentNodeCode;
	}

	public String getSelectFlag() {
		if (NodeCode == null) {
			init();
		}
		return SelectFlag;
	}

	public void setSelectFlag(String selectFlag) {
		SelectFlag = selectFlag;
	}

	public TreeTableRow(TreeTable treeTable, Object[] values) {
		this.treeTable = treeTable;
		treeTable.addRow(this);
		this.Values = values;
	}

	public void init() {
		this.ColCount = Values.length;
		this.NodeCode = Values[0].toString();
		this.ParentNodeCode = Values[1].toString();
		this.NodeName = Values[2].toString();
		this.ChildFlag = Values[3].toString();
		this.NodeOrder = Values[ColCount - 3].toString();
		this.SelectFlag = Values[ColCount - 2].toString();
		this.Level = Values[ColCount - 1].toString();
	}

	public TreeTable getTreeTable() {
		return treeTable;
	}

	public void setTreeTable(TreeTable treeTable) {
		this.treeTable = treeTable;
		treeTable.addRow(this);
	}

	public Object get(int index) {
		if (Values == null) {
			return null;
		}
		if (index < 0 || index >= Values.length) {
			throw new RuntimeException("DataRow中没有指定的列：" + index);
		}
		return Values[index];
	}

	public String getString(int index) {
		Object o = get(index);
		if (o == null) {
			return null;
		} else {
			return String.valueOf(o);
		}
	}

	public Object get(String columnName) {
		if (columnName == null || columnName.equals("")) {
			throw new RuntimeException("不能存取列名为空的列");
		}
		for (int i = 0; i < Values.length; i++) {
			if (this.treeTable.getColumns()[i].getColumnName().equalsIgnoreCase(columnName)) {
				return Values[i];
			}
		}
		throw new RuntimeException("指定的列名没有找到");
	}

	public void set(int index, Object value) {
		if (Values == null) {
			return;
		}
		if (index < 0 || index >= Values.length) {
			throw new RuntimeException("DataRow中没有指定的列：" + index);
		}
		Values[index] = value;
	}

	public void set(String columnName, Object value) {
		if (columnName == null || columnName.equals("")) {
			throw new RuntimeException("不能存取列名为空的列");
		}
		for (int i = 0; i < Values.length; i++) {
			if (this.treeTable.getColumns()[i].getColumnName().equalsIgnoreCase(columnName)) {
				Values[i] = value;
			}
		}
		throw new RuntimeException("指定的列名没有找到");

	}
}
