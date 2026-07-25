import { useEffect, useState } from "react";
import { FileText } from "lucide-react";
import { getMyStatement } from "../services/ownerService";
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
import "../styles/OwnerStatementPage.css";

function OwnerStatementPage() {
  const [statement, setStatement] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const fetchStatement = async () => {
      try {
        setLoading(true);
        setError("");

        const data = await getMyStatement();
        setStatement(data);
      } catch (err) {
        setError(err.message || "Failed to load your statement.");
      } finally {
        setLoading(false);
      }
    };

    fetchStatement();
  }, []);

  return (
    <PageShell
      className="owner-statement-page"
      eyebrow="Owner finance"
      title="My account statement"
      description="View your balance, monthly charges, and hotel-related transactions."
      toolbar={<BackButton to="/hotel-owner/hotels" label="Back to hotels" />}
    >
      {loading ? (
        <LoadingState label="Loading statement..." />
      ) : error ? (
        <EmptyState title="Statement could not be loaded" description={error} />
      ) : (
        <>
          <div className="ui-stat-grid">
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
              description="All visible charges and balance adjustments."
              actions={<FileText size={20} />}
            />

            {!statement?.entries || statement.entries.length === 0 ? (
              <EmptyState title="No transactions found" description="Transactions will appear here when posted." />
            ) : (
              <DataTable aria-label="Owner statement transactions">
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

export default OwnerStatementPage;
