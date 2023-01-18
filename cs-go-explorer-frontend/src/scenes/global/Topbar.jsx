import { useContext } from "react";
import { Box, IconButton, useTheme } from "@mui/material";
import InputBase from "@mui/material/InputBase";
import LightModeOutlinedIcon from "@mui/icons-material/LightModeOutlined";
import DarkModeOutlinedIcon from "@mui/icons-material/DarkModeOutlined";
import NotificationsOutlinedIcon from "@mui/icons-material/NotificationsOutlined";
import SettingsOutlinedIcon from "@mui/icons-material/SettingsOutlined";
import PersonOutlinedIcon from "@mui/icons-material/PersonOutlined";
import SearchIcon from "@mui/icons-material/Search";
import { motion } from "framer-motion";
import { ColorModeContext } from "../../theme";

const Topbar = () => {
	const theme = useTheme();
	const colorMode = useContext(ColorModeContext);

	return (
		<Box display="flex" justifyContent="space-between" padding={2}>
			{/* SEARCH BAR */}
			<Box
				display="flex"
				backgroundColor="custom.steamColorB"
				borderRadius="3px"
			>
				<InputBase sx={{ marginLeft: 2, flex: 1 }} placeholder="Search"/>
				<IconButton type="button" sx={{ padding: 1, color: "custom.steamColorD" }}>
					<SearchIcon/>
				</IconButton>
			</Box>

			{/* ICONS */}
			<Box display="flex">
				<motion.div whileHover={{ scale: 1.2 }}>
					<IconButton onClick={colorMode.toggleColorMode}>
						{theme.palette.mode === "dark" ? (
							<DarkModeOutlinedIcon sx={{ color: "custom.steamColorD" }}/>
						) : (
							<LightModeOutlinedIcon/>
						)}
					</IconButton>
				</motion.div>
				<motion.div whileHover={{ scale: 1.2 }}>
					<IconButton>
						<NotificationsOutlinedIcon sx={{ color: "custom.steamColorD" }}/>
					</IconButton>
				</motion.div>
				<motion.div whileHover={{ scale: 1.2 }}>
					<IconButton>
						<SettingsOutlinedIcon sx={{ color: "custom.steamColorD" }}/>
					</IconButton>
				</motion.div>
				<motion.div whileHover={{ scale: 1.2 }}>
					<IconButton>
						<PersonOutlinedIcon sx={{ color: "custom.steamColorD" }}/>
					</IconButton>
				</motion.div>
			</Box>
		</Box>
	);
};

export default Topbar;