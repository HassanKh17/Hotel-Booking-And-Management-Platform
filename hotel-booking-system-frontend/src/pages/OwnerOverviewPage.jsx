import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { Building2, User } from "lucide-react";
import { getOwnerOverview } from "../services/ownerOverviewService";
import MessageAlert from "../components/admin/MessageAlert";
import parseError from "../utils/parseError";
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
import { formatCurrency, getBalanceClassName } from "../utils/formatters";
import "../styles/AdminPage.css";

function OwnerOverviewPage() {
  const { ownerId } = useParams();

  const [ownerOverview, setOwnerOverview] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const loadOwnerOverview = async () => {
      try {
        setLoading(true);
        setError("");

        const data = await getOwnerOverview(ownerId);
        setOwnerOverview(data);
      } catch (err) {
        setError(parseError(err));
      } finally {
        setLoading(false);
      }
    };

    loadOwnerOverview();
  }, [ownerId]);

  const owner = ownerOverview?.owner;
  const hotels = ownerOverview?.hotels || [];

  return (
    <PageShell
      className="admin-page"
      eyebrow="Admin portal"
      title="Owner overview"
      description="View owner details and the hotels registered under this account."
      toolbar={<BackButton to="/admin" label="Back to dashboard" />}
    >
      <MessageAlert type="error" message={error} />

      {loading ? (
        <LoadingState label="Loading owner overview..." />
      ) : (
        <>
          {owner && (
            <Card>
              <SectionHeader title="Owner details" description="Account and balance information." actions={<User size={20} />} />
              <div className="ui-stat-grid">
                <div className="ui-stat-card">
                  <span>Owner ID</span>
                  <strong>{owner.ownerId}</strong>
                </div>
                <div className="ui-stat-card">
                  <span>Username</span>
                  <strong>{owner.username}</strong>
                </div>
                <div className="ui-stat-card">
                  <span>Email</span>
                  <strong>{owner.email}</strong>
                </div>
                <div className="ui-stat-card">
                  <span>Balance</span>
                  <strong className={getBalanceClassName(owner.balance)}>{formatCurrency(owner.balance)}</strong>
                </div>
              </div>
            </Card>
          )}

          <Card>
            <SectionHeader
              title="Hotels"
              description={`${hotels.length} hotel${hotels.length === 1 ? "" : "s"} registered to this owner.`}
              actions={<Building2 size={20} />}
            />

            {hotels.length === 0 ? (
              <EmptyState title="No hotels found" description="This owner has not registered hotels yet." />
            ) : (
              <DataTable aria-label="Owner hotels">
                <thead>
                  <tr>
                    <th>Hotel ID</th>
                    <th>Name</th>
                    <th>City</th>
                    <th>Star rating</th>
                    <th>Special offer</th>
                  </tr>
                </thead>
                <tbody>
                  {hotels.map((hotel) => (
                    <tr key={hotel.hotelId}>
                      <td>{hotel.hotelId}</td>
                      <td>{hotel.name}</td>
                      <td>{hotel.city}</td>
                      <td>{hotel.starRating}</td>
                      <td>
                        <Badge tone={hotel.specialOfferPct > 0 ? "warning" : "neutral"}>
                          {hotel.specialOfferPct ?? 0}%
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

export default OwnerOverviewPage;
