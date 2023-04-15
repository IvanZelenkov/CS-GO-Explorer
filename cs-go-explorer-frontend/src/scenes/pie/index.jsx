import { Box, Button } from "@mui/material";
import Header from "../../components/Header";
import PieChart from "../../components/PieChart";
import { useNavigate } from "react-router-dom";
import { useOutletContext } from "react-router-dom";
import { motion } from "framer-motion";
import KeyboardBackspaceRoundedIcon from "@mui/icons-material/KeyboardBackspaceRounded";

const Pie = () => {
	let navigation = useNavigate();
	const { chartState, chartSubtitle } = useOutletContext();

	return (
		<motion.div exit={{ opacity: 0 }}>
			<Box margin="1.5vh">
				<Box display="flex" alignItems="center" flexDirection="row">
					<Button
						sx={{
							backgroundColor: "custom.steamColorC",
							color: "custom.steamColorD",
							fontWeight: "bold",
							margin: "0 2.5vh 2.5vh 0",
							width: "2vw",
							height: "3vh",
							border: `0.2vh solid #5ddcff`,
							borderRadius: "1vh",
							boxShadow: "0px 0px 10px #5ddcff",
							":hover": {
								backgroundColor: "custom.steamColorB"
							}
						}}
						onClick={() => navigation(-1)}
					>
						<KeyboardBackspaceRoundedIcon fontSize="large"/>
					</Button>
					<Header title="Pie Chart" subtitle={chartSubtitle}/>
				</Box>
				<Box height="80vh">
					<PieChart chartState={chartState} chartSubtitle={chartSubtitle}/>
				</Box>
			</Box>
		</motion.div>
	);
};

export default Pie;