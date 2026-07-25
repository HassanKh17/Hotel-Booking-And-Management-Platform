import { useEffect, useState } from "react";
import { CreditCard, RefreshCw, Search, UserPlus } from "lucide-react";
import OwnerTable from "../components/admin/OwnerTable";
import OwnerFormModal from "../components/admin/OwnerFormModal";
import MessageAlert from "../components/admin/MessageAlert";
import { getOwners, registerOwner, editOwner, removeOwner } from "../services/adminService";
import parseError from "../utils/parseError";
import {
  Button,
  Card,
  ConfirmDialog,
  LoadingState,
  PageShell,
  SectionHeader,
} from "../components/ui";
import { formatCurrency, getBalanceClassName } from "../utils/formatters";
import "../styles/AdminPage.css";

function AdminPage() {
  const [owners, setOwners] = useState([]);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(false);
  const [modalLoading, setModalLoading] = useState(false);
  const [search, setSearch] = useState("");
  const [ownerToDelete, setOwnerToDelete] = useState(null);

  const [isCreateOpen, setIsCreateOpen] = useState(false);
  const [isEditOpen, setIsEditOpen] = useState(false);
  const [selectedOwner, setSelectedOwner] = useState(null);

  const loadOwners = async () => {
    setLoading(true);
    setError("");

    try {
      const data = await getOwners();
      setOwners(data);
    } catch (err) {
      setError(parseError(err));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadOwners();
  }, []);

  const handleCreateOwner = async (formData) => {
    setModalLoading(true);
    setError("");
    setSuccess("");

    try {
      await registerOwner(formData);
      setSuccess("Hotel owner added successfully.");
      setIsCreateOpen(false);
      await loadOwners();
    } catch (err) {
      setError(parseError(err));
    } finally {
      setModalLoading(false);
    }
  };

  const handleEditClick = (owner) => {
    setSelectedOwner(owner);
    setIsEditOpen(true);
    setError("");
    setSuccess("");
  };

  const handleEditOwner = async (formData) => {
    setModalLoading(true);
    setError("");
    setSuccess("");

    const payload = {
      username: formData.username,
      email: formData.email,
    };

    if (formData.password?.trim()) {
      payload.password = formData.password;
    }

    try {
      await editOwner(selectedOwner.id, payload);
      setSuccess("Hotel owner updated successfully.");
      setIsEditOpen(false);
      setSelectedOwner(null);
      await loadOwners();
    } catch (err) {
      setError(parseError(err));
    } finally {
      setModalLoading(false);
    }
  };

  const handleDeleteOwner = async () => {
    if (!ownerToDelete) return;

    setError("");
    setSuccess("");

    try {
      await removeOwner(ownerToDelete.id);
      setSuccess("Hotel owner removed successfully.");
      setOwnerToDelete(null);
      await loadOwners();
    } catch (err) {
      setError(parseError(err));
    }
  };

  const filteredOwners = owners.filter((owner) => {
    const term = search.trim().toLowerCase();
    if (!term) return true;
    return (
      owner.username?.toLowerCase().includes(term) ||
      owner.email?.toLowerCase().includes(term) ||
      String(owner.id).includes(term)
    );
  });

  const totalBalance = owners.reduce((sum, owner) => sum + Number(owner.balance ?? 0), 0);

  return (
    <PageShell
      className="admin-page"
      eyebrow="Admin portal"
      title="Admin dashboard"
      description="Manage hotel owners, account balances, and system-wide pricing settings."
      actions={
        <>
          <Button icon={<UserPlus size={17} />} onClick={() => setIsCreateOpen(true)}>
            Add owner
          </Button>
          <Button variant="secondary" icon={<RefreshCw size={17} />} onClick={loadOwners}>
            Refresh
          </Button>
          <Button to="/admin/global-charges" variant="dark" icon={<CreditCard size={17} />}>
            Global charges
          </Button>
        </>
      }
    >
      <div className="ui-stat-grid">
        <div className="ui-stat-card">
          <span>Total owners</span>
          <strong>{owners.length}</strong>
        </div>
        <div className="ui-stat-card">
          <span>Combined balance</span>
          <strong className={getBalanceClassName(totalBalance)}>{formatCurrency(totalBalance)}</strong>
        </div>
        <div className="ui-stat-card">
          <span>Visible results</span>
          <strong>{filteredOwners.length}</strong>
        </div>
      </div>

      <Card>
        <SectionHeader
          title="Hotel owners"
          description="Search, edit, review statements, and open owner overviews."
          actions={
            <div className="hotel-filter admin-search">
              <label htmlFor="ownerSearch">
                <Search size={14} /> Search owners
              </label>
              <input
                id="ownerSearch"
                type="search"
                placeholder="ID, username, or email"
                value={search}
                onChange={(event) => setSearch(event.target.value)}
              />
            </div>
          }
        />

        <MessageAlert type="error" message={error} />
        <MessageAlert type="success" message={success} />

        {loading ? (
          <LoadingState label="Loading owners..." />
        ) : (
          <OwnerTable owners={filteredOwners} onEdit={handleEditClick} onDelete={setOwnerToDelete} />
        )}
      </Card>

      <OwnerFormModal
        isOpen={isCreateOpen}
        mode="create"
        owner={null}
        onClose={() => setIsCreateOpen(false)}
        onSubmit={handleCreateOwner}
        loading={modalLoading}
      />

      <OwnerFormModal
        isOpen={isEditOpen}
        mode="edit"
        owner={selectedOwner}
        onClose={() => {
          setIsEditOpen(false);
          setSelectedOwner(null);
        }}
        onSubmit={handleEditOwner}
        loading={modalLoading}
      />

      <ConfirmDialog
        open={Boolean(ownerToDelete)}
        title="Remove hotel owner?"
        message={`This will remove ${ownerToDelete?.username ?? "this owner"} from the system.`}
        confirmLabel="Remove"
        onCancel={() => setOwnerToDelete(null)}
        onConfirm={handleDeleteOwner}
      />
    </PageShell>
  );
}

export default AdminPage;
