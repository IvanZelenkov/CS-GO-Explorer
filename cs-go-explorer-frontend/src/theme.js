import { createContext, useState, useMemo, useEffect } from "react";
import { createTheme } from "@mui/material/styles";

// Color design tokens
export const tokens = (mode) => ({
	...(mode === "dark"
		? {
			grey: {
				100: "#e0e0e0",
				200: "#c2c2c2",
				300: "#a3a3a3",
				400: "#858585",
				500: "#666666",
				600: "#525252",
				700: "#3d3d3d",
				800: "#292929",
				900: "#141414"
			},
			primary: {
				100: "#d0d1d5",
				200: "#a1a4ab",
				300: "#727681",
				400: "#1F2A40",
				500: "#141b2d",
				600: "#101624",
				700: "#0c101b",
				800: "#080b12",
				900: "#040509"
			},
			steamColors: {
				1: "#171a21",
				2: "#1b2838",
				3: "#2a475e",
				4: "#FFFFFF",
				5: "#5ddcff",
				6: "#5ddcff",
				7: "#ccba7c"
			}
		}
		: {
			grey: {
				100: "#141414",
				200: "#292929",
				300: "#3d3d3d",
				400: "#525252",
				500: "#666666",
				600: "#858585",
				700: "#a3a3a3",
				800: "#c2c2c2",
				900: "#e0e0e0"
			},
			primary: {
				100: "#040509",
				200: "#080b12",
				300: "#0c101b",
				400: "#f2f0f0",
				500: "#141b2d",
				600: "#1F2A40",
				700: "#727681",
				800: "#a1a4ab",
				900: "#d0d1d5"
			},
			steamColors: {
				1: "#171a21",
				2: "#1b2838",
				3: "#2a475e",
				4: "#FFFFFF",
				5: "#5ddcff",
				6: "#00FF00FF",
				7: "#ccba7c"
			}
		})
});

// MUI theme settings
export const themeSettings = (mode) => {
	const colors = tokens(mode);
	return {
		palette: {
			mode: mode,
			...(mode === "dark"
				? {
					// palette values for dark mode
					primary: {
						main: colors.grey[100]
					},
					secondary: {
						main: colors.grey[900]
					},
					custom: {
						steamColorA: colors.steamColors[1],
						steamColorB: colors.steamColors[2],
						steamColorC: colors.steamColors[3],
						steamColorD: colors.steamColors[4],
						steamColorE: colors.steamColors[5],
						steamColorF: colors.steamColors[6],
						steamColorG: colors.steamColors[7]
					},
					background: {
						default: colors.steamColors[1]
					}
				}
				: {
					// palette values for light mode
					primary: {
						main: colors.grey[100]
					},
					secondary: {
						main: colors.grey[900]
					},
					custom: {
						steamColorA: colors.steamColors[4],
						steamColorB: colors.steamColors[4],
						steamColorC: colors.steamColors[4],
						steamColorD: colors.steamColors[2],
						steamColorE: colors.steamColors[5],
						steamColorF: colors.steamColors[6],
						steamColorG: colors.steamColors[7]
					},
					background: {
						default: colors.steamColors[7]
					}
				})
		},
		typography: {
			fontFamily: ["Source Sans Pro", "sans-serif"].join(","),
			fontSize: 12,
			h1: {
				fontFamily: ["Source Sans Pro", "sans-serif"].join(","),
				fontSize: 40
			},
			h2: {
				fontFamily: ["Source Sans Pro", "sans-serif"].join(","),
				fontSize: 32
			},
			h3: {
				fontFamily: ["Source Sans Pro", "sans-serif"].join(","),
				fontSize: 24
			},
			h4: {
				fontFamily: ["Source Sans Pro", "sans-serif"].join(","),
				fontSize: 20
			},
			h5: {
				fontFamily: ["Source Sans Pro", "sans-serif"].join(","),
				fontSize: 16
			},
			h6: {
				fontFamily: ["Source Sans Pro", "sans-serif"].join(","),
				fontSize: 14
			}
		}
	};
};

// Context for color mode
export const ColorModeContext = createContext({
	toggleColorMode: () => {}
});

export const useMode = () => {
	const [mode, setMode] = useState("dark");

	// Setting local storage for theme mode
	useEffect(() => {
		const theme = JSON.parse(localStorage.getItem("theme"));
		if (theme)
			setMode(theme);
	}, []);

	useEffect(() => {
		localStorage.setItem("theme", JSON.stringify(mode));
	}, [mode]);

	const colorMode = useMemo(() => ({
			toggleColorMode: () =>
				setMode((prev) => (prev === "light" ? "dark" : "light")),
		}),
		[]
	);

	const theme = useMemo(() => createTheme(themeSettings(mode)), [mode]);
	return [theme, colorMode];
};

export const muiPaginationCSS = {
	"& .MuiPaginationItem-root": {
		color: "#5ddcff",
		marginRight: "2vw",
		'&:hover, &:focus': {
			backgroundColor: "#5ddcff",
			color: "#000000"
		}
	},
	"& .Mui-selected": {
		color: "#000000",
		backgroundColor: "#5ddcff",
		"&:hover &:focus": {
			backgroundColor: "#000000",
			color: "#ffffff",
			filter: "brightness(0.75)"
		}
	},
	"& .MuiPaginationItem-page:not(.Mui-selected)": {
		backgroundColor: "#000000",
		"&:hover": {
			color: "#000000",
			backgroundColor: "#ffffff"
		}
	},
	'& .Mui-selected:not(:focus)': {
		color: "#000000",
		backgroundColor: "#5ddcff",
		"&:hover": {
			backgroundColor: "#489ab5",
			filter: "brightness(0.75)"
		}
	},
	marginTop: "1.5vh"
}

export function muiTextFieldCSS(themeColor) {
	return {
		"& .MuiOutlinedInput-root .MuiOutlinedInput-notchedOutline": {
			borderColor: themeColor
		},
		"&:hover .MuiOutlinedInput-root .MuiOutlinedInput-notchedOutline": {
			borderColor: themeColor
		},
		"& .MuiOutlinedInput-root.Mui-focused .MuiOutlinedInput-notchedOutline": {
			borderColor: themeColor
		},
		"& .MuiOutlinedInput-input": {
			color: themeColor
		},
		"&:hover .MuiOutlinedInput-input": {
			color: themeColor
		},
		"& .MuiOutlinedInput-root.Mui-focused .MuiOutlinedInput-input": {
			color: themeColor
		},
		"& .MuiInputLabel-outlined": {
			color: themeColor
		},
		"&:hover .MuiInputLabel-outlined": {
			color: themeColor
		},
		"& .MuiInputLabel-outlined.Mui-focused": {
			color: themeColor
		},
		boxShadow: "0px 0px 10px #5ddcff",
		width: "20vw",
		borderRadius: "5px"
	}
}