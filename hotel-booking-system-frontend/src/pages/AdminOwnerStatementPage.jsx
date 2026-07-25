import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { FileText } from "lucide-react";
import { getOwnerStatement } from "../services/adminService";
import {
  Badge,
  BackButton,
  Card,
  DataTable,
  EmptyState,
  LoadingState,
  PageShell,
  SectionHeader,
} from "../components/ui";
import {
  formatCurrency,
  formatDateTime,
  formatStatementType,
  getBalanceClassName,
} from "../utils/formatters";
import "../styles/AdminOwnerStatementPage.css";

function AdminOwnerStatementPage() {
  const { ownerId } = useParams();

  const [statement, setStatement] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const fetchStatement = async () => {
      try {
        setLoading(true);
        setError("");

        const data = await getOwnerStatement(ownerId);
        setStatement(data);
      } catch (err) {
        setError(err.message || "Failed to load owner statement.");
      } finally {
        setLoading(false);
      }
    };

    fetchStatement();
  }, [ownerId]);

  return (
    <PageShell
      className="statement-page"
      eyebrow="Admin finance"
      title="Owner account statement"
      description="Review monthly charges and account balance details for this hotel owner."
      toolbar={<BackButton to="/admin" label="Back to dashboard" />}
    >
      {loading ? (
        <LoadingState label="Loading statement..." />
      ) : error ? (
        <EmptyState title="Statement could not be loaded" description={error} />
      ) : (
        <>
          <div className="ui-stat-grid">
            <div className="ui-stat-card">
              <span>Owner ID</span>
              <strong>{statement?.ownerId}</strong>
            </div>
            <div className="ui-stat-card">
              <span>Username</span>
              <strong>{statement?.username}</strong>
            </div>
            <div className="ui-stat-card">
              <span>Email</span>
              <strong>{statement?.email}</strong>
            </div>
            <div className="ui-stat-card">
              <span>Current balance</span>
              <strong className={getBalanceClassName(statement?.currentBalance)}>
                {formatCurrency(statement?.currentBalance)}
              </strong>
            </div>
          </div>

          <Card>
            <SectionHeader
              title="Transactions"
              description="All visible charges and balance adjustments for this owner."
              actions={<FileText size={20} />}
            />

            {!statement?.entries || statement.entries.length === 0 ? (
              <EmptyState
                title="No transactions found"
                description="Transactions for this owner will appear here when posted."
              />
            ) : (
              <DataTable aria-label="Admin owner statement transactions">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Date</th>
                    <th>Type</th>
                    <th>Description</th>
                    <th>Hotel ID</th>
                    <th>Hotel name</th>
                    <th>Amount</th>
                  </tr>
                </thead>
                <tbody>
                  {statement.entries.map((entry) => (
                    <tr key={entry.id}>
                      <td>{entry.id}</td>
                      <td>{formatDateTime(entry.createdAt)}</td>
                      <td>{formatStatementType(entry.type)}</td>
                      <td>{entry.description}</td>
                      <td>{entry.hotelId ?? "-"}</td>
                      <td>{entry.hotelName ?? "-"}</td>
                      <td>
                        <Badge tone={Number(entry.amount) < 0 ? "danger" : "success"}>
                          {formatCurrency(entry.amount)}
                        </Badge>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </DataTable>
            )}
          </Card>
        </>
      )}
    </PageShell>
  );
}

export default AdminOwnerStatementPage;
