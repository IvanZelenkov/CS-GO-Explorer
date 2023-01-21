import { Navigate, Outlet } from "react-router-dom";

const ProtectedRoute = ({ isAllowed, redirectPath = "/steam-id-form"}) => {
	if (!isAllowed) {
		return <Navigate to={redirectPath} replace/>;
	} else {
		return (
			<Outlet/>
		);
	}
};

export default ProtectedRoute;