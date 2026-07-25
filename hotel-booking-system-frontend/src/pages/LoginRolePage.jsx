import { Building2, ShieldCheck, UserRound } from "lucide-react";
import { Card, PageShell, RoleChoiceCard, SectionHeader } from "../components/ui";

function LoginRolePage() {
  return (
    <PageShell
      className="login-role-page"
      eyebrow="Sign in"
      title="Choose your portal"
      description="Select the account type you want to access."
      narrow
    >
      <Card>
        <SectionHeader
          title="Continue as"
          description="Each role has a separate login destination and dashboard."
        />

        <div className="role-choice-grid">
          <RoleChoiceCard
            to="/login/customer"
            icon={<UserRound size={26} />}
            title="Customer"
            description="Book hotels, manage reservations, and update your account."
          />
          <RoleChoiceCard
            to="/login/admin"
            icon={<ShieldCheck size={26} />}
            title="Admin"
            description="Manage owners, statements, and platform-wide charges."
          />
          <RoleChoiceCard
            to="/login/hotel-owner"
            icon={<Building2 size={26} />}
            title="Hotel Owner"
            description="Manage hotels, rooms, occupancy, and owner statements."
          />
        </div>
      </Card>
    </PageShell>
  );
}

export default LoginRolePage;
