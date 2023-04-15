import { Typography, Box } from '@mui/material';

const Header = ({ title, subtitle }) => {

	return (
		<Box sx={{ marginBottom: "2.5vh" }}>
			<Typography sx={{
				fontSize: "2.5vh",
				color: "custom.steamColorD",
				fontWeight: "bold",
				fontFamily: "Montserrat"
			}}>
				{title}
			</Typography>
			<Typography sx={{
				fontSize: "1.5vh",
				color: "custom.steamColorE",
				fontWeight: "bold",
				fontFamily: "Montserrat"
			}}>
				{subtitle}
			</Typography>
		</Box>
	);
}

export default Header;