import { Link } from "react-router-dom";
import { BarChart3, Edit3, FileText, Trash2 } from "lucide-react";
import { Button, DataTable, EmptyState } from "../ui";
import { formatCurrency, getBalanceClassName } from "../../utils/formatters";

function OwnerTable({ owners, onEdit, onDelete }) {
  if (!owners.length) {
    return (
      <EmptyState
        title="No hotel owners found"
        description="Add an owner to start assigning and managing hotels."
      />
    );
  }

  return (
    <DataTable aria-label="Hotel owners">
      <thead>
        <tr>
          <th>ID</th>
          <th>Username</th>
          <th>Email</th>
          <th>Balance</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        {owners.map((owner) => (
          <tr key={owner.id}>
            <td>{owner.id}</td>
            <td>{owner.username}</td>
            <td>{owner.email}</td>
            <td>
              {owner.balance !== null && owner.balance !== undefined
                ? <span className={getBalanceClassName(owner.balance)}>{formatCurrency(owner.balance)}</span>
                : "-"}
            </td>
            <td>
              <div className="ui-actions">
                <Button variant="secondary" size="sm" icon={<Edit3 size={14} />} onClick={() => onEdit(owner)}>
                  Edit
                </Button>
                <Button variant="danger" size="sm" icon={<Trash2 size={14} />} onClick={() => onDelete(owner)}>
                  Remove
                </Button>
                <Button
                  as={Link}
                  to={`/admin/owners/${owner.id}/overview`}
                  variant="primary"
                  size="sm"
                  icon={<BarChart3 size={14} />}
                >
                  Overview
                </Button>
                <Button
                  as={Link}
                  to={`/admin/owners/${owner.id}/statement`}
                  variant="success"
                  size="sm"
                  icon={<FileText size={14} />}
                >
                  Statement
                </Button>
              </div>
            </td>
          </tr>
        ))}
      </tbody>
    </DataTable>
  );
}

export default OwnerTable;
