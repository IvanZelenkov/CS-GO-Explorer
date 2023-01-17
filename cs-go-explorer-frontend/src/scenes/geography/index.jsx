import { Box, useTheme } from "@mui/material";
import GeographyChart from "../../components/GeographyChart";
import Header from "../../components/Header";
import { tokens } from "../../theme";
import { motion } from "framer-motion";

const Geography = () => {
	const theme = useTheme();
	const colors = tokens(theme.palette.mode);
	return (
		<motion.div exit={{ opacity: 0 }}>
			<Box m="20px">
				<Header title="Geography" subtitle="Simple Geography Chart"/>

				<Box
					height="75vh"
					border={`1px solid ${colors.grey[100]}`}
					borderRadius="4px"
				>
					<GeographyChart/>
				</Box>
			</Box>
		</motion.div>
	);
};

export default Geography;