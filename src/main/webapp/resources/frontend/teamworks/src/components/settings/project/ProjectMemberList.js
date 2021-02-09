import { Link } from "react-router-dom";
import { useContext } from "react/cjs/react.development";
import UserCredentials from "../../../context/UserCredentials";
import "../../../FontStyles.css";
import ProjectApiUtils from "../../../utils/api/ProjectApiUtils";
import Spinner from "../../spinner/Spinner";
import "../UserList.css";

const ProjectMemberList = ({projectId, members, loading, onListUpdated}) => {

  const credentials = useContext(UserCredentials);
  const isProjectManager = credentials.isProjectManager(projectId);

  function onPromoteClicked(member)
  {
    if (member.isProjectManager)
    {
      // TODO: Change confirm message if it's project manager and not team manager who is asking
      if (window.confirm(`Are you sure to demote ${member.name} ${member.lastname} and make them a member?`))
      {
        ProjectApiUtils.addUserToProject(projectId, member.userId, false)
          .then(() => onListUpdated())
          .catch(err => console.error(err));
      }
    } else
    {
      // TODO: Change confirm message if it's project manager and not team manager who is asking
      if (window.confirm(`Are you sure to promote ${member.name} ${member.lastname} to project manager?`))
      {
        ProjectApiUtils.addUserToProject(projectId, member.userId, true)
          .then(() => onListUpdated())
          .catch(err => console.error(err));
      }
    }

  }

  function onRemoveClicked(member)
  {
    if (window.confirm(`Are you sure to remove ${member.name} ${member.lastname} from the project?`))
    {
      ProjectApiUtils.removeUserFromProject(projectId, member.userId)
      .then(() => onListUpdated())
      .catch(err => console.error(err));
    }
  }

  if (loading)
  {
    return <Spinner />
  }

  let elements;
  if (members && members.length > 0)
  {
    elements = members.map(member =>
    {
      const completename = member.name + " " + member.lastname;
      const username = completename.toLowerCase().replace(/ /g, "");
      let actionStrip;
      if (isProjectManager)
      {
        actionStrip = (
          <td className="ActionStrip">
            <span onClick={() => onPromoteClicked(member)} className="BoldTitle BoldTitle--Smallest ActionButton">{member.isProjectManager ? "Demote" : "Promote"}</span>
            <span onClick={() => onRemoveClicked(member)} className="BoldTitle BoldTitle--Smallest ActionButton">Remove</span>
          </td>
        );
      }
      return (
        <tr key={member.id}>
          <td><Link to={`/settings/users/${member.userId}/${username}`}>{member.lastname}, {member.name}</Link></td>
          <td>{member.email}</td>
          <td>{member.isProjectManager ? "Proj. Manager" : "Member"}</td>
          <td>{member.initialDate}</td>
          {actionStrip}
        </tr>
      );
    });
  } else
  {
    return <p>This project has no members yet.</p>
  }

  let actionRow;
  if (isProjectManager)
    actionRow = <th>Actions</th>
  return (
    <table className="UserList">
      <thead className="UserList__thead">
        <tr>
          <th>Name</th>
          <th>E-mail address</th>
          <th>Role</th>
          <th>Member since</th>
          {actionRow}
        </tr>
      </thead>
      <tbody>
        {elements}
      </tbody>
    </table>
  );
};

export default ProjectMemberList;
