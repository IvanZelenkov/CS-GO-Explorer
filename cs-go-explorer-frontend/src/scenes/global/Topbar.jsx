import { useContext } from "react";
import { Box, IconButton, Tooltip, useTheme } from "@mui/material";
import InputBase from "@mui/material/InputBase";
import LightModeOutlinedIcon from "@mui/icons-material/LightModeOutlined";
import DarkModeOutlinedIcon from "@mui/icons-material/DarkModeOutlined";
import LogoutIcon from '@mui/icons-material/Logout';
import PersonOutlinedIcon from "@mui/icons-material/PersonOutlined";
import SearchIcon from "@mui/icons-material/Search";
import { motion } from "framer-motion";
import { useNavigate } from "react-router-dom";
import { ColorModeContext } from "../../theme";

const Topbar = ({ userDenied }) => {
	const theme = useTheme();
	const colorMode = useContext(ColorModeContext);
	const navigate = useNavigate();

	const signOut = () => {
		localStorage.setItem("is_user_allowed", "deny");
		userDenied(localStorage.getItem("is_user_allowed"));
	}

	return (
		<Box display="flex" justifyContent="space-between" padding="2vh">
			{/* SEARCH BAR */}
			<Box
				display="flex"
				backgroundColor="custom.steamColorB"
				borderRadius="3px"
			>
				{/*<InputBase sx={{ marginLeft: 2, flex: 1 }} placeholder="Search"/>*/}
				{/*<IconButton type="button" sx={{ padding: 1, color: "custom.steamColorD" }}>*/}
				{/*	<SearchIcon/>*/}
				{/*</IconButton>*/}
			</Box>

			{/* ICONS */}
			<Box display="flex">
				<motion.div whileHover={{ scale: 1.2 }} style={{ marginRight: "1vh" }}>
					<Tooltip title="Logout" placement="bottom">
						<IconButton onClick={signOut} sx={{
							border: `0.2vh solid #5ddcff`,
							boxShadow: "0px 0px 10px #5ddcff"
						}}>
							<LogoutIcon sx={{
								fontSize: "1.8vh",
								color: "custom.steamColorD",
								borderRadius: "1vh",
								":hover": {
									color: "#5ddcff"
								}
							}}/>
						</IconButton>
					</Tooltip>
				</motion.div>
				<motion.div whileHover={{ scale: 1.2 }} style={{ marginRight: "1vh" }}>
					{theme.palette.mode === "dark" ? (
						<Tooltip title="Dark Theme" placement="bottom">
							<IconButton onClick={colorMode.toggleColorMode} sx={{
								border: `0.2vh solid #5ddcff`,
								boxShadow: "0px 0px 10px #5ddcff"
							}}>
								<DarkModeOutlinedIcon sx={{
									fontSize: "1.8vh",
									color: "custom.steamColorD",
									borderRadius: "1vh",
									":hover": {
										color: "#5ddcff"
									}
								}}/>
							</IconButton>
						</Tooltip>
					) : (
						<Tooltip title="Light Theme" placement="bottom" sx={{ marginRight: "1vh" }}>
							<IconButton onClick={colorMode.toggleColorMode} sx={{
								border: `0.2vh solid #5ddcff`,
								boxShadow: "0px 0px 10px #5ddcff"
							}}>
								<LightModeOutlinedIcon sx={{
									fontSize: "1.8vh",
									color: "custom.steamColorD",
									borderRadius: "1vh",
									":hover": {
										color: "#5ddcff"
									}
								}}/>
							</IconButton>
						</Tooltip>
					)}
				</motion.div>
				<motion.div whileHover={{ scale: 1.2 }} style={{ marginRight: "1vh" }}>
					<Tooltip title="View Profile" placement="bottom">
						<IconButton onClick={() => navigate("/profile")} sx={{
							border: `0.2vh solid #5ddcff`,
							boxShadow: "0px 0px 10px #5ddcff"
						}}>
							<PersonOutlinedIcon sx={{
								fontSize: "1.8vh",
								color: "custom.steamColorD",
								borderRadius: "1vh",
								":hover": {
									color: "#5ddcff"
								}
							}}/>
						</IconButton>
					</Tooltip>
				</motion.div>
			</Box>
		</Box>
	);
};

export default Topbar;