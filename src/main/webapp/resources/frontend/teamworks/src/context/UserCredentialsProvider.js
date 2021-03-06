import { useEffect } from "react";
import { useState } from "react";
import UserApiUtils from "../utils/api/UserApiUtils"
import UserCredentials from "./UserCredentials";

const UserCredentialsProvider = ({ children }) =>
{
    const [credentials, setCredentials] = useState({
        isDepartmentManager: () => false,
        isProjectManager: () => false,
        getRoleName: () => "Member",

    });

    // right now if there's a breaking change we'll just reload the page. 
    // but there should be a way to request context provider refresh from children

    useEffect(() =>
    {
        UserApiUtils.getCredentials()
            .then(c =>
            {
                c.isDepartmentManager = (departmentId) =>
                {
                    departmentId = Number(departmentId);
                    if (c.isTeamManager)
                        return true;
                    const dpt = c.currentDepartments?.find(x => x.id === departmentId);
                    if (!dpt)
                        return false;
                    return dpt.isDepartmentManager;
                };
                c.isProjectManager = (projectId, departmentId) =>
                {
                    projectId = Number(projectId);
                    departmentId = Number(departmentId);
                    if (c.isTeamManager)
                        return true;
                    if (departmentId && c.isDepartmentManager(departmentId))
                        return true; 
                    const proj = c.currentProjects?.find(x => x.id === projectId);
                    if (!proj)
                        return false;
                    return proj.isProjectManager;
                };
                c.getRoleName = () =>
                {
                    let role = "Member";
                    if (c.isTeamManager)
                        role = "Team manager";
                    else if (c.currentDepartments?.some(x => x.isDepartmentManager && x.finalDate === null))
                        role = "Department manager";
                    else if (c.currentProjects?.some(x => x.isProjectManager && x.finalDate === null))
                        role = "Project manager";
                    return role;
                }
                console.log(c);
                setCredentials(c);
            })
            .catch(err => console.error(err))
    }, []);

    return (
        <UserCredentials.Provider value={credentials}>
            {children}
        </UserCredentials.Provider>
    );
}

export default UserCredentialsProvider;